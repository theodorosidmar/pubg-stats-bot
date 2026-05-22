package dev.pubgstats.bot.discord.command.duel.kills

import dev.pubgkt.GameMode
import dev.pubgkt.stats.lifetime.LifetimeGameModeStats
import dev.pubgstats.bot.discord.command.BotLocale
import dev.pubgstats.bot.discord.command.COLOR_GREEN
import dev.pubgstats.bot.discord.command.COLOR_YELLOW
import dev.pubgstats.bot.discord.command.Embed
import dev.pubgstats.bot.discord.command.GREEN_CIRCLE
import dev.pubgstats.bot.discord.command.Localized
import dev.pubgstats.bot.discord.command.RED_CIRCLE
import dev.pubgstats.bot.discord.command.WHITE_CIRCLE
import java.util.Locale

fun buildDuelKillsEmbed(
    playerOneName: String,
    playerTwoName: String,
    playerOneStats: LifetimeGameModeStats,
    playerTwoStats: LifetimeGameModeStats,
): Localized<Embed> {
    var playerOneScore = 0
    var playerTwoScore = 0

    val fields = DuelKillsStat.entries.map { stat ->
        val p1Value = playerOneStats.statValue(stat)
        val p2Value = playerTwoStats.statValue(stat)
        when {
            p1Value > p2Value -> playerOneScore++
            p2Value > p1Value -> playerTwoScore++
        }
        StatComparison(stat, p1Value, p2Value)
    }

    val color = if (playerOneScore == playerTwoScore) COLOR_YELLOW else COLOR_GREEN

    val result = DuelKillsResult(
        playerOneName = playerOneName,
        playerTwoName = playerTwoName,
        gameMode = playerOneStats.gameMode,
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

private fun LifetimeGameModeStats.statValue(stat: DuelKillsStat): Double = when (stat) {
    DuelKillsStat.DAMAGE_DEALT -> stats.damageDealt
    DuelKillsStat.HEADSHOT_KILLS -> stats.headshotKills.toDouble()
    DuelKillsStat.KILLS -> stats.kills.toDouble()
    DuelKillsStat.LONGEST_KILL -> stats.longestKill
    DuelKillsStat.ROAD_KILLS -> stats.roadKills.toDouble()
    DuelKillsStat.ROUND_MOST_KILLS -> stats.roundMostKills.toDouble()
}

private data class StatComparison(
    val stat: DuelKillsStat,
    val p1Value: Double,
    val p2Value: Double,
) {
    fun toField(locale: BotLocale, playerOneName: String, playerTwoName: String): Embed.Field {
        val p1Formatted = formatStat(stat, p1Value, locale)
        val p2Formatted = formatStat(stat, p2Value, locale)
        val value = when {
            p1Value > p2Value ->
                "$GREEN_CIRCLE **$playerOneName**: $p1Formatted  |  $RED_CIRCLE $playerTwoName: $p2Formatted"

            p2Value > p1Value ->
                "$RED_CIRCLE $playerOneName: $p1Formatted  |  $GREEN_CIRCLE **$playerTwoName**: $p2Formatted"

            else ->
                "$WHITE_CIRCLE $playerOneName: $p1Formatted  |  $WHITE_CIRCLE $playerTwoName: $p2Formatted"
        }
        return Embed.Field(
            name = DuelKillsMessages.statLabel(stat).resolve(locale),
            value = value,
        )
    }

    private fun formatStat(stat: DuelKillsStat, value: Double, locale: BotLocale): String {
        val javaLocale = when (locale) {
            BotLocale.EN_US -> Locale.US
            BotLocale.PT_BR -> Locale.of("pt", "BR")
        }
        return when (stat) {
            DuelKillsStat.DAMAGE_DEALT,
            DuelKillsStat.LONGEST_KILL,
            -> String.format(javaLocale, "%,.2f", value)

            else -> String.format(javaLocale, "%,d", value.toInt())
        }
    }
}

private data class DuelKillsResult(
    val playerOneName: String,
    val playerTwoName: String,
    val gameMode: GameMode,
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
            title = DuelKillsMessages.title(playerOneName, playerTwoName).resolve(locale),
            description = DuelKillsMessages
                .description(gameMode.name.replace(oldValue = "_", newValue = " "))
                .resolve(locale),
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
        DuelKillsMessages.scoreLine(playerOneName, playerOneScore, playerTwoScore).resolve(locale)

    playerTwoScore > playerOneScore ->
        DuelKillsMessages.scoreLine(playerTwoName, playerTwoScore, playerOneScore).resolve(locale)

    else ->
        DuelKillsMessages.tiedScore(playerOneScore).resolve(locale)
}
