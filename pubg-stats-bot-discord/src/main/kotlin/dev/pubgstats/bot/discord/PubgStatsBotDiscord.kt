package dev.pubgstats.bot.discord

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.pubgstats.bot.LifetimeCommand
import dev.pubgstats.bot.PubgStatsBot
import dev.pubgstats.bot.X1Command
import org.slf4j.LoggerFactory
import pubgkt.PubgSteamApi
import pubgkt.Stats

class PubgStatsBotDiscord(
    private val token: String,
) : PubgStatsBot(
    pubgApi = PubgSteamApi(System.getenv("PUBG_API_KEY") ?: error("PUBG API Key required"))
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @OptIn(PrivilegedIntent::class)
    suspend fun init() {
        Kord(token).apply {
            on<MessageCreateEvent> {
                if (!message.isValid()) return@on
                val response = getResponse(message.content)
                message.channel.createMessage(response)
            }
        }.login {
            intents += Intent.MessageContent
            logger.info("Logged in successfully")
        }
    }

    override suspend fun outputLifetime(command: LifetimeCommand, stats: Stats): String =
        outputGetLifetimeStats(command.player, command.gameMode.id, stats)

    override suspend fun outputX1(
        command: X1Command,
        playerOneStats: Stats,
        playerTwoStats: Stats,
    ): String = outputX1(
        command.gameMode.name,
        command.playerOne,
        playerOneStats,
        command.playerTwo,
        playerTwoStats,
    )
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
