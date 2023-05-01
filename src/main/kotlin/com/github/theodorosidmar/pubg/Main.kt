package com.github.theodorosidmar.pubg

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.serialization
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("Main")
val commands = setOf(
    "!squad",
    "!duo",
    "!solo",
)

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val token = System.getenv("DISCORD_TOKEN") ?: error("Discord bot token required")
    Kord(token).apply {
        on<MessageCreateEvent> {
            if (message.author?.isBot!!) return@on
            val (command, player) = message.content.split(' ', limit = 2)
            if (command !in commands || player.isEmpty()) return@on
            val pubgClient = HttpClient(CIO) {
                install(ContentNegotiation) {
                    serialization(
                        ContentType.Application.Json,
                        Json {
                            ignoreUnknownKeys = true
                            encodeDefaults = true
                            isLenient = true
                            allowSpecialFloatingPointValues = true
                            allowStructuredMapKeys = true
                            prettyPrint = false
                            useArrayPolymorphism = false
                        },
                    )
                }
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
                defaultRequest {
                    bearerAuth(pubgApiKey)
                    contentType(ContentType("application", "vnd.api+json"))
                }
            }
            val accountId: String = pubgClient.get("$pubgApiPath$playerPath") {
                url {
                    parameters.append("filter[playerNames]", player)
                }
            }.body<PlayerResponse>().data.first().id
            val lifetime: LifetimeResponse = pubgClient.get("$pubgApiPath/seasons/lifetime/gameMode/${command.removePrefix("!")}-fpp/players") {
                url {
                    parameters.append("filter[playerIds]", accountId)
                }
            }.body()
            message.channel.createMessage(output(lifetime.data.first().attributes.gameModeStats["${command.removePrefix("!")}-fpp"]!!))
        }
    }.login {
        intents += Intent.MessageContent
        logger.info("Logged in successfully")
    }
}

fun output(stats: Stats): String {
    return with(stats) {
        """
        Seus stats:
        Armas looteadas: $weaponsAcquired
        Assistencias: $assists
        Boosts: $boosts
        Dano causado: $damageDealt
        Distancia dirigida: $rideDistance
        Distancia nadada: $swimDistance
        Distancia percoridda: $walkDistance
        Fogo amigo: $teamKills
        Headshot kills: $headshotKills
        Heals: $heals
        Kills: $kills
        Kills em uma unica partida: $roundMostKills
        Kill mais longe: $longestKill
        Kills por atropelamento: $roadKills
        Kill streak: $maxKillStreaks
        Knocks: $dBNOs
        Loses: $losses
        Partidas jogadas: $roundsPlayed
        Revives: $revives
        Suicidios: $suicides
        Tempo sobrevivido: $timeSurvived
        Tempo sobrevivido (recorde): $longestTimeSurvived
        Top 10: $top10s
        Veiculos destruidos: $vehicleDestroys
        """.trimIndent()
    }
}

val pubgApiKey = System.getenv("PUBG_API_KEY") ?: error("PUBG API Key required")
const val pubgApiPath = "https://api.pubg.com/shards/steam"
const val playerPath = "/players"

@Serializable
data class PlayerResponse(val data: List<Player>)

@Serializable
data class Player(val id: String)

@Serializable
data class LifetimeResponse(val data: List<Lifetime>)

@Serializable
data class Lifetime(val attributes: Attributes)

@Serializable
data class Attributes(val gameModeStats: Map<String, Stats>)

@Serializable
data class GameModeStats(val name: Stats)

@Serializable
data class Stats(
    val assists: Int,
    val boosts: Int,
    val dBNOs: Int,
    val dailyKills: Int,
    val dailyWins: Int,
    val damageDealt: Double,
    val days: Int,
    val headshotKills: Int,
    val heals: Int,
    val killPoints: Int,
    val kills: Int,
    val longestKill: Double,
    val longestTimeSurvived: Double,
    val losses: Int,
    val maxKillStreaks: Int,
    val mostSurvivalTime: Double,
    val rankPoints: Int,
    val rankPointsTitle: String,
    val revives: Int,
    val rideDistance: Double,
    val roadKills: Int,
    val roundMostKills: Int,
    val roundsPlayed: Int,
    val suicides: Int,
    val swimDistance: Double,
    val teamKills: Int,
    val timeSurvived: Double,
    val top10s: Int,
    val vehicleDestroys: Int,
    val walkDistance: Double,
    val weaponsAcquired: Int,
    val weeklyKills: Int,
    val weeklyWins: Int,
    val winPoints: Int,
    val wins: Int,
)
