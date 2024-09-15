package dev.pubgstats.bot.discord

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import org.slf4j.LoggerFactory
import pubgkt.GameMode
import pubgkt.PubgSteamApi
import pubgkt.Stats

class PubgStatsBotDiscord(private val token: String) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val pubgApi = PubgSteamApi(System.getenv("PUBG_API_KEY") ?: error("PUBG API Key required"))

    @OptIn(PrivilegedIntent::class)
    suspend fun init() {
        Kord(token).apply {
            on<MessageCreateEvent> {
                if (message.isNotValid()) return@on
                val player = message.content.split(' ').last()
                val gameMode = GameMode.SquadFpp
                val stats = pubgApi.getLifetimeStats(player, gameMode)
                message.channel.createMessage(output(player, gameMode.name, stats.getOrThrow()))
            }
        }.login {
            intents += Intent.MessageContent
            logger.info("Logged in successfully")
        }
    }
}

private fun output(player: String, gameMode: String, stats: Stats): String = with(stats) {
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
