package com.github.theodorosidmar.pubgstats

import com.github.theodorosidmar.pubg.GameMode
import com.github.theodorosidmar.pubg.PubgClient
import com.github.theodorosidmar.pubg.Stats
import com.github.theodorosidmar.pubgstats.commons.titlecase
import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import org.slf4j.LoggerFactory

class PubgStatsBot(private val token: String) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val pubgClient = PubgClient(System.getenv("PUBG_API_KEY") ?: error("PUBG API Key required"))

    @OptIn(PrivilegedIntent::class)
    suspend fun init() {
        Kord(token = token).apply {
            on<MessageCreateEvent> {
                if (!message.isValid()) return@on
                val (command, player) = message.toCommandAndPlayerName()
                pubgClient.getLifetimeStats(player, GameMode.valueOf(command.name.titlecase()))
                    ?.let { output(player, command.name, it) }
                    ?.run { message.channel.createMessage(this) }
            }
        }.login {
            intents += Intent.MessageContent
            logger.info("Logged in successfully")
        }
    }
}

typealias PlayerName = String

private fun Message.isValid(): Boolean =
    this.content.startsWith(Command.prefix) && !this.author?.isBot!!

private fun Message.toCommandAndPlayerName(): Pair<Command, PlayerName> {
    val (first, second) = this.content.split(' ', limit = 2)
    return Command(first) to second
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
