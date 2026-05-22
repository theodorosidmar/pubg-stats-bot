package dev.pubgstats.bot.discord.command.duel.weapon

import dev.pubgkt.mastery.WeaponSummary
import dev.pubgstats.bot.discord.command.Embed
import dev.pubgstats.bot.discord.command.Localized
import dev.pubgstats.bot.discord.command.duel.buildDuelEmbed

fun buildDuelWeaponEmbed(
    playerOneName: String,
    playerTwoName: String,
    weaponName: Weapon,
    playerOneStats: WeaponSummary,
    playerTwoStats: WeaponSummary,
): Localized<Embed> = buildDuelEmbed(
    playerOneName = playerOneName,
    playerTwoName = playerTwoName,
    description = Localized(
        enUs = "${weaponName.label.uppercase()} DUEL",
        ptBr = "DUELO DE ${weaponName.label.uppercase()}",
    ),
    stats = DuelWeaponStat.entries,
    statValue = { stat -> playerOneStats.statValue(stat) to playerTwoStats.statValue(stat) },
)

private fun WeaponSummary.statValue(stat: DuelWeaponStat): Double = when (stat) {
    DuelWeaponStat.XP_TOTAL -> xpTotal.toDouble()

    DuelWeaponStat.CURRENT_LEVEL -> currentLevel.toDouble()

    DuelWeaponStat.CURRENT_TIER -> currentTier.toDouble()

    DuelWeaponStat.MOST_DAMAGE_IN_GAME -> statsTotal.mostDamagePlayerInAGame

    DuelWeaponStat.TOTAL_DAMAGE -> maxOf(
        statsTotal.damagePlayer,
        officialStatsTotal.damagePlayer,
        competitiveStatsTotal.damagePlayer,
    )

    DuelWeaponStat.MOST_HEADSHOTS_IN_GAME -> statsTotal.mostHeadShotsInAGame.toDouble()

    DuelWeaponStat.TOTAL_HEADSHOTS -> maxOf(
        statsTotal.headShots,
        officialStatsTotal.headShots,
        competitiveStatsTotal.kills,
    ).toDouble()

    DuelWeaponStat.KILLS -> maxOf(statsTotal.kills, officialStatsTotal.kills, competitiveStatsTotal.kills).toDouble()

    DuelWeaponStat.MOST_KILLS_IN_GAME -> maxOf(
        statsTotal.mostKillsInAGame,
        officialStatsTotal.mostKillsInAGame,
        competitiveStatsTotal.mostKillsInAGame,
    ).toDouble()
}
