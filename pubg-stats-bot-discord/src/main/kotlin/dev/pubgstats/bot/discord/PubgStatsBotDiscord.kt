package dev.pubgstats.bot.discord

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient
import org.slf4j.LoggerFactory
import pubgkt.GameMode
import pubgkt.PubgSteamApi

class PubgStatsBotDiscord(
    private val token: String,
    applicationId: String,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val interaction = RestClient(KtorRequestHandler(token)).interaction
    private val pubgApi = PubgSteamApi(System.getenv("PUBG_API_KEY") ?: error("PUBG API Key required"))
    private val snowflake = Snowflake(applicationId)

    suspend fun init() {
        registerCommands()
        login()
    }

    private suspend fun registerCommands() {
        interaction.createGlobalChatInputApplicationCommand(
            applicationId = snowflake,
            name = "kills",
            description = "See your amount of kills",
        ) {
            string("player", "The nickname of the player") {
                required = true
            }
        }
    }

    @OptIn(PrivilegedIntent::class)
    private suspend fun login() {
        Kord(token)
            .apply {
                on<GuildChatInputCommandInteractionCreateEvent> {
                    logger.info("Received command user=${interaction.user.globalName} command=${interaction.command.rootName}")
                    val response = interaction.deferPublicResponse()
                    val player = interaction.command.strings["player"]!!
                    val stats = pubgApi.getLifetimeStats(player, GameMode.SquadFpp).getOrThrow()
                    response.respond {
                        content = "`$player` killed **${stats.kills}** playing **${GameMode.SquadFpp.id}**"
                    }
                }
            }
            .login {
                intents += Intent.MessageContent
                logger.info("Logged in successfully")
            }
    }
}
