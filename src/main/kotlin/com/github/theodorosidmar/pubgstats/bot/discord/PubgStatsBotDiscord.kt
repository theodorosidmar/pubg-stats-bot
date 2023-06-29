package com.github.theodorosidmar.pubgstats.bot.discord

import com.github.theodorosidmar.pubgstats.bot.Command
import com.github.theodorosidmar.pubgstats.bot.PubgStatsBot
import com.github.theodorosidmar.pubgstats.bot.commons.logger
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import pubgkt.PubgApi
import pubgkt.Stats

class PubgStatsBotDiscord(private val token: String) : PubgStatsBot {
    private val logger by logger()
    private val pubgClient = PubgApi(System.getenv("PUBG_API_KEY") ?: error("PUBG API Key required"))

    @OptIn(PrivilegedIntent::class)
    suspend fun init() {
        Kord(token).apply {
            on<MessageCreateEvent> {
                if (!message.isValid()) return@on
                val (command, player) = message.toCommandAndPlayerName()
                val stats = getLifetimeStats(player, command)
                message.channel.createMessage(stats)
            }
        }.login {
            intents += Intent.MessageContent
            logger.info("Logged in successfully")
        }
    }

    override suspend fun getLifetimeStats(player: String, command: Command): String =
        pubgClient.getLifetimeStats(player, command.toGameMode())
            .getOrThrow()
            .let { output(player, command.name, it) }
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
