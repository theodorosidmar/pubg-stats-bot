package dev.pubgstats.bot.discord

import dev.kord.core.Kord
import dev.pubgkt.ExponentialBackoff
import dev.pubgkt.PubgApi
import dev.pubgkt.Retry
import dev.pubgkt.ratelimit.ConcurrentDelayRateLimiter
import dev.pubgstats.bot.discord.command.CommandHandler
import dev.pubgstats.bot.discord.command.PingHandler
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
    private val commandsDispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
    private val commandsScope = CoroutineScope(
        SupervisorJob() + commandsDispatcher + CoroutineName("CommandProcessor"),
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

    private val handlers: Map<String, CommandHandler<*>> = listOf(
        PingHandler(),
    ).associateBy { it.name }

    private val registrar = CommandRegistrar(handlers)
    private val dispatcher = CommandDispatcher(handlers, commandsScope)

    suspend fun run() {
        logger.debug("Initializing Kord with {} gateway worker(s)", config.gatewayWorkers)
        val kord = Kord(config.discordToken) {
            defaultDispatcher = gatewayDispatcher
        }

        registrar.register(kord)
        dispatcher.attach(kord)

        logger.info("Logging in to Discord")
        kord.login()
    }
}
