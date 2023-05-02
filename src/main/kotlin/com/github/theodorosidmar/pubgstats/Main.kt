package com.github.theodorosidmar.pubgstats

import com.github.theodorosidmar.pubgstats.pubg.PubgClient
import com.github.theodorosidmar.pubgstats.pubg.Stats
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import org.slf4j.LoggerFactory

internal val commands = setOf("solo", "duo", "squad")
internal val logger = LoggerFactory.getLogger("PubgStatsBot")
internal const val prefix = "!"

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val token = System.getenv("DISCORD_TOKEN") ?: error("Discord bot token required")
    val pubgClient = PubgClient()
    Kord(token).apply {
        on<MessageCreateEvent> {
            if (message.author?.isBot!!) return@on
            if (!message.content.startsWith(prefix)) return@on
            val (command, player) = runCatching {
                message.content.split(' ', limit = 2)
            }.getOrElse { return@on }
            val commandWithoutPrefix = command.removePrefix(prefix)
            if (commandWithoutPrefix !in commands || player.isEmpty()) return@on
            pubgClient.getLifetimeStats(player, commandWithoutPrefix)
                ?.let { output(player, commandWithoutPrefix, it) }
                ?.run { message.channel.createMessage(this) }
        }
    }.login {
        intents += Intent.MessageContent
        logger.info("Logged in successfully")
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
    Distância percoridda: $walkDistance
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
