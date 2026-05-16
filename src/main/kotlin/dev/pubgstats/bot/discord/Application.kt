package dev.pubgstats.bot.discord

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.DeferredMessageInteractionResponseBehavior
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.boolean
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.user
import dev.pubgkt.ExponentialBackoff
import dev.pubgkt.PubgApi
import dev.pubgkt.Retry
import dev.pubgkt.ratelimit.ConcurrentDelayRateLimiter
import dev.pubgkt.ratelimit.RateLimitExceededException
import dev.pubgstats.bot.discord.command.CommandHandler
import dev.pubgstats.bot.discord.command.CommandOption
import dev.pubgstats.bot.discord.command.PingHandler
import dev.pubgstats.bot.discord.command.Visibility
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.concurrent.Executors

class Application(private val config: Config) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val gatewayDispatcher = Executors.newFixedThreadPool(config.gatewayWorkers).asCoroutineDispatcher()
    private val commandsDispatchers = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
    private val commandsScope = CoroutineScope(
        SupervisorJob() + commandsDispatchers + CoroutineName("CommandProcessor"),
    )

    @Suppress("unused")
    private val pubgApi = PubgApi(
        apiKey = config.pubgApiKey,
        rateLimiter = ConcurrentDelayRateLimiter(),
        retry = Retry(
            maxRetries = 3,
            backoff = ExponentialBackoff(),
            retryOnExceptions = listOf(IOException::class),
        ),
    )

    private val commandHandlers: Map<String, CommandHandler<*>> =
        listOf(
            PingHandler(),
        ).associateBy {
            it.name
        }

    suspend fun run() {
        logger.debug("Initializing Kord with {} gateway worker(s)", config.gatewayWorkers)
        val kord = Kord(token = config.discordToken) {
            defaultDispatcher = gatewayDispatcher
        }

        registerCommands(kord)
        registerHandlers(kord)

        logger.info("Logging in to Discord")
        kord.login()
        logger.info("Logged in to Discord")
    }

    private suspend fun registerCommands(kord: Kord) {
        commandHandlers.values.forEach { handler ->
            logger.debug("Registering slash command: /{} ({})", handler.name, handler.description)
            kord.createGlobalChatInputCommand(handler.name, handler.description) {
                buildOptions(handler.options)
            }
        }
        logger.info("Registered {} slash command(s)", commandHandlers.size)
    }

    private fun registerHandlers(kord: Kord) {
        kord.on<ChatInputCommandInteractionCreateEvent>(scope = commandsScope) {
            val commandName = interaction.command.rootName
            val handler = commandHandlers[commandName]
            if (handler == null) {
                logger.debug("No handler found for command: /{}", commandName)
                return@on
            }
            logger.debug("Dispatching /{} to {}", commandName, handler::class.simpleName)
            val deferred = deferResponse(interaction, handler.visibility)
            val ctx = KordCommandContext(interaction, deferred)
            try {
                handler.handle(ctx)
            } catch (e: RateLimitExceededException) {
                logger.warn("Rate limit exceeded while handling /{}", commandName, e)
                ctx.respond("Rate limit exceeded. Please try again in a moment.")
            }
        }
    }

    private suspend fun deferResponse(
        interaction: ChatInputCommandInteraction,
        visibility: Visibility,
    ): DeferredMessageInteractionResponseBehavior = when (visibility) {
        Visibility.PUBLIC -> interaction.deferPublicResponse()
        Visibility.EPHEMERAL -> interaction.deferEphemeralResponse()
    }
}

private fun BaseInputChatBuilder.buildOptions(options: List<CommandOption>) {
    options.forEach { option ->
        when (option) {
            is CommandOption.StringOption -> string(option.name, option.description) {
                required = option.required
                option.choices.forEach { choice(it.label, it.value) }
            }

            is CommandOption.IntegerOption -> integer(option.name, option.description) {
                required = option.required
            }

            is CommandOption.BooleanOption -> boolean(option.name, option.description) {
                required = option.required
            }

            is CommandOption.UserOption -> user(option.name, option.description) {
                required = option.required
            }
        }
    }
}
