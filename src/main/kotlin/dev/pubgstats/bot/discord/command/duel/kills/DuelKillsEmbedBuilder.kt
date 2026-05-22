package dev.pubgstats.bot.discord.command.duel.kills

import dev.pubgkt.stats.lifetime.LifetimeGameModeStats
import dev.pubgstats.bot.discord.command.Embed
import dev.pubgstats.bot.discord.command.Localized
import dev.pubgstats.bot.discord.command.duel.buildDuelEmbed

fun buildDuelKillsEmbed(
    playerOneName: String,
    playerTwoName: String,
    playerOneStats: LifetimeGameModeStats,
    playerTwoStats: LifetimeGameModeStats,
): Localized<Embed> {
    val gameMode = playerOneStats.gameMode.name.replace(oldValue = "_", newValue = " ")
    return buildDuelEmbed(
        playerOneName = playerOneName,
        playerTwoName = playerTwoName,
        description = Localized(
            enUs = "KILLS DUEL AT $gameMode",
            ptBr = "DUELO DE MORTES NO $gameMode",
        ),
        stats = DuelKillsStat.entries,
        statValue = { stat -> playerOneStats.statValue(stat) to playerTwoStats.statValue(stat) },
    )
}

private fun LifetimeGameModeStats.statValue(stat: DuelKillsStat): Double = when (stat) {
    DuelKillsStat.DAMAGE_DEALT -> stats.damageDealt
    DuelKillsStat.HEADSHOT_KILLS -> stats.headshotKills.toDouble()
    DuelKillsStat.KILLS -> stats.kills.toDouble()
    DuelKillsStat.LONGEST_KILL -> stats.longestKill
    DuelKillsStat.ROAD_KILLS -> stats.roadKills.toDouble()
    DuelKillsStat.ROUND_MOST_KILLS -> stats.roundMostKills.toDouble()
}
