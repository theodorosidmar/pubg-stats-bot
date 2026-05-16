package dev.pubgstats.bot.discord

import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.DeferredMessageInteractionResponseBehavior
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.pubgkt.ratelimit.RateLimitExceededException
import dev.pubgstats.bot.discord.command.CommandHandler
import dev.pubgstats.bot.discord.command.Localized
import dev.pubgstats.bot.discord.command.Visibility
import kotlinx.coroutines.CoroutineScope
import org.slf4j.LoggerFactory

class CommandDispatcher(
    private val handlers: Map<String, CommandHandler<*>>,
    private val scope: CoroutineScope,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun attach(kord: Kord) {
        kord.on<ChatInputCommandInteractionCreateEvent>(scope = scope) {
            val commandName = interaction.command.rootName
            val handler = handlers[commandName]
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
                ctx.respond(RATE_LIMIT_MESSAGE.resolve(ctx.locale))
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

    companion object {
        private val RATE_LIMIT_MESSAGE = Localized(
            enUs = "Rate limit exceeded. Please try again in a moment.",
            ptBr = "Limite de requisições excedido. Tente novamente em instantes.",
        )
    }
}
