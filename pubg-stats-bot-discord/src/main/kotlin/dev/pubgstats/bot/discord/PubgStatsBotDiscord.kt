package dev.pubgstats.bot.discord

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.pubgstats.bot.Command
import dev.pubgstats.bot.LifetimeCommand
import dev.pubgstats.bot.PubgStatsBot
import dev.pubgstats.bot.X1Command
import kotlinx.coroutines.Dispatchers
import pubgkt.PubgSteamApi
import pubgkt.Stats
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

class PubgStatsBotDiscord(private val token: String) : PubgStatsBot {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val pubgClient = PubgSteamApi(System.getenv("PUBG_API_KEY") ?: error("PUBG API Key required"))

    @OptIn(PrivilegedIntent::class)
    suspend fun init() {
        Kord(token).apply {
            on<MessageCreateEvent> {
                if (!message.isValid()) return@on
                val command = message.toCommand()
                val response = if (command is X1Command) {
                    val (_, first, second) = message.content.split(' ')
                    x1(first, second, command)
                } else {
                    val (_, player) = message.content.split(' ', limit = 2)
                    getLifetimeStats(player, command)
                }
                message.channel.createMessage(response)
            }
        }.login {
            intents += Intent.MessageContent
            logger.info("Logged in successfully")
        }
    }

    override suspend fun getLifetimeStats(player: String, command: Command): String =
        with(command as LifetimeCommand) {
            pubgClient.getLifetimeStats(player, command.gameMode)
                .getOrThrow()
                .let { outputGetLifetimeStats(player, command.gameMode.id, it) }
        }

    override suspend fun x1(playerOne: String, playerTwo: String, command: Command): String {
        command as X1Command
        val (playerOneStats, playerTwoStats) =
            withContext(Dispatchers.IO) {
                awaitAll(
                    async { pubgClient.getLifetimeStats(playerOne, command.gameMode) },
                    async { pubgClient.getLifetimeStats(playerTwo, command.gameMode) }
                )
            }
        return outputX1(
            command.gameMode.name,
            playerOne,
            playerOneStats.getOrThrow(),
            playerTwo,
            playerTwoStats.getOrThrow(),
        )
    }
}

private fun outputGetLifetimeStats(player: String, gameMode: String, stats: Stats): String = with(stats) {
    """
    Estatísticas de $player desde sempre no modo $gameMode-fpp:
    Armas looteadas: $weaponsAcquired
    Assistências: $assists
    Boosts: $boosts
    Dano causado: $damageDealt
    Distância dirigida: $rideDistance
    Distância nadada: $swimDistance
    Distância percorrida: $walkDistance
    Fogo amigo: $teamKills
    Headshot kills: $headshotKills
    Heals: $heals
    Kills: $kills
    Kills em uma única partida: $roundMostKills
    Kill mais longe: $longestKill
    Kills por atropelamento: $roadKills
    Kill streak: $maxKillStreaks
    Knocks: $dBNOs
    Loses: $losses
    Partidas jogadas: $roundsPlayed
    Revives: $revives
    Suicídios: $suicides
    Tempo sobrevivido: $timeSurvived
    Tempo sobrevivido (recorde): $longestTimeSurvived
    Top 10: $top10s
    Veículos destruídos: $vehicleDestroys
    """.trimIndent()
}

private fun outputX1(
    gameMode: String,
    playerOne: String,
    playerOneStats: Stats,
    playerTwo: String,
    playerTwoStats: Stats,
): String {
    var output = "Jogando $gameMode, o"
    output += if (playerOneStats.kills > playerTwoStats.kills) {
        " $playerOne matou mais do que o $playerTwo. ${playerOneStats.kills} kills contra ${playerTwoStats.kills}"
    } else {
        " $playerTwo matou mais do que o $playerOne. ${playerTwoStats.kills} kills contra ${playerOneStats.kills}"
    }
    return output
}
