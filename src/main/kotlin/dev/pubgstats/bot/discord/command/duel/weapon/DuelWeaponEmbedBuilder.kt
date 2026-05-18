package dev.pubgstats.bot.discord.command.duel.weapon

import dev.pubgkt.mastery.WeaponSummary
import dev.pubgstats.bot.discord.command.BotLocale
import dev.pubgstats.bot.discord.command.COLOR_GREEN
import dev.pubgstats.bot.discord.command.COLOR_YELLOW
import dev.pubgstats.bot.discord.command.Embed
import dev.pubgstats.bot.discord.command.GREEN_CIRCLE
import dev.pubgstats.bot.discord.command.Localized
import dev.pubgstats.bot.discord.command.RED_CIRCLE
import dev.pubgstats.bot.discord.command.WHITE_CIRCLE
import java.util.Locale

fun buildDuelWeaponEmbed(
    playerOneName: String,
    playerTwoName: String,
    weaponName: Weapon,
    playerOneStats: WeaponSummary,
    playerTwoStats: WeaponSummary,
): Localized<Embed> {
    var playerOneScore = 0
    var playerTwoScore = 0

    val fields = DuelWeaponStat.entries.map { stat ->
        val p1Value = playerOneStats.statValue(stat)
        val p2Value = playerTwoStats.statValue(stat)
        when {
            p1Value > p2Value -> playerOneScore++
            p2Value > p1Value -> playerTwoScore++
        }
        StatComparison(stat, p1Value, p2Value)
    }

    val color = if (playerOneScore == playerTwoScore) COLOR_YELLOW else COLOR_GREEN

    val result = DuelWeaponResult(
        playerOneName = playerOneName,
        playerTwoName = playerTwoName,
        weaponName = weaponName.label,
        comparisons = fields,
        playerOneScore = playerOneScore,
        playerTwoScore = playerTwoScore,
        color = color,
    )

    return Localized(
        enUs = result.toEmbed(BotLocale.EN_US),
        ptBr = result.toEmbed(BotLocale.PT_BR),
    )
}

private data class DuelWeaponResult(
    val playerOneName: String,
    val playerTwoName: String,
    val weaponName: String,
    val comparisons: List<StatComparison>,
    val playerOneScore: Int,
    val playerTwoScore: Int,
    val color: Int,
) {
    fun toEmbed(locale: BotLocale): Embed {
        val statFields = comparisons.map { it.toField(locale, playerOneName, playerTwoName) }
        val scoreField = Embed.Field(
            name = "Score",
            value = scoreText(locale, playerOneName, playerTwoName, playerOneScore, playerTwoScore),
        )
        return Embed(
            title = DuelWeaponMessages.title(playerOneName, playerTwoName).resolve(locale),
            description = DuelWeaponMessages.description(weaponName).resolve(locale),
            color = color,
            fields = statFields + scoreField,
        )
    }
}

private fun scoreText(
    locale: BotLocale,
    playerOneName: String,
    playerTwoName: String,
    playerOneScore: Int,
    playerTwoScore: Int,
): String = when {
    playerOneScore > playerTwoScore ->
        DuelWeaponMessages.scoreLine(playerOneName, playerOneScore, playerTwoScore).resolve(locale)

    playerTwoScore > playerOneScore ->
        DuelWeaponMessages.scoreLine(playerTwoName, playerTwoScore, playerOneScore).resolve(locale)

    else ->
        DuelWeaponMessages.tiedScore(playerOneScore).resolve(locale)
}

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

private data class StatComparison(
    val stat: DuelWeaponStat,
    val p1Value: Double,
    val p2Value: Double,
) {
    fun toField(locale: BotLocale, playerOneName: String, playerTwoName: String): Embed.Field {
        val p1Formatted = formatStat(stat, p1Value)
        val p2Formatted = formatStat(stat, p2Value)
        val value = when {
            p1Value > p2Value ->
                "$GREEN_CIRCLE **$playerOneName**: $p1Formatted  |  $RED_CIRCLE $playerTwoName: $p2Formatted"

            p2Value > p1Value ->
                "$RED_CIRCLE $playerOneName: $p1Formatted  |  $GREEN_CIRCLE **$playerTwoName**: $p2Formatted"

            else ->
                "$WHITE_CIRCLE $playerOneName: $p1Formatted  |  $WHITE_CIRCLE $playerTwoName: $p2Formatted"
        }
        return Embed.Field(
            name = DuelWeaponMessages.statLabel(stat).resolve(locale),
            value = value,
        )
    }

    private fun formatStat(stat: DuelWeaponStat, value: Double): String = when (stat) {
        DuelWeaponStat.MOST_DAMAGE_IN_GAME,
        DuelWeaponStat.TOTAL_DAMAGE,
        ->
            String.format(Locale.US, "%.2f", value)

        else -> value.toInt().toString()
    }
}
