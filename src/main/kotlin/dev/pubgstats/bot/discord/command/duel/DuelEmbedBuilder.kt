package dev.pubgstats.bot.discord.command.duel

import dev.pubgstats.bot.discord.command.BotLocale
import dev.pubgstats.bot.discord.command.COLOR_GREEN
import dev.pubgstats.bot.discord.command.COLOR_YELLOW
import dev.pubgstats.bot.discord.command.Embed
import dev.pubgstats.bot.discord.command.GREEN_CIRCLE
import dev.pubgstats.bot.discord.command.Localized
import dev.pubgstats.bot.discord.command.RED_CIRCLE
import dev.pubgstats.bot.discord.command.WHITE_CIRCLE
import java.util.Locale

fun <S : DuelStat> buildDuelEmbed(
    playerOneName: String,
    playerTwoName: String,
    description: Localized<String>,
    stats: List<S>,
    statValue: (S) -> Pair<Double, Double>,
): Localized<Embed> {
    var p1Score = 0
    var p2Score = 0

    val comparisons = stats.map { stat ->
        val (p1, p2) = statValue(stat)
        val cmp = stat.comparator.compare(p1, p2)
        when {
            cmp > 0 -> p1Score++
            cmp < 0 -> p2Score++
        }
        StatComparison(stat, p1, p2)
    }

    val color = if (p1Score == p2Score) COLOR_YELLOW else COLOR_GREEN
    val title = DuelMessages.title(playerOneName, playerTwoName)

    return Localized(
        enUs = toEmbed(
            locale = BotLocale.EN_US,
            title = title,
            description = description,
            comparisons = comparisons,
            playerOneName = playerOneName,
            playerTwoName = playerTwoName,
            p1Score = p1Score,
            p2Score = p2Score,
            color = color,
        ),
        ptBr = toEmbed(
            locale = BotLocale.PT_BR,
            title = title,
            description = description,
            comparisons = comparisons,
            playerOneName = playerOneName,
            playerTwoName = playerTwoName,
            p1Score = p1Score,
            p2Score = p2Score,
            color = color,
        ),
    )
}

@Suppress("LongParameterList")
private fun toEmbed(
    locale: BotLocale,
    title: Localized<String>,
    description: Localized<String>,
    comparisons: List<StatComparison>,
    playerOneName: String,
    playerTwoName: String,
    p1Score: Int,
    p2Score: Int,
    color: Int,
): Embed {
    val statFields = comparisons.map { it.toField(locale, playerOneName, playerTwoName) }
    val scoreField = Embed.Field(
        name = "Score",
        value = scoreText(locale, playerOneName, playerTwoName, p1Score, p2Score),
    )
    return Embed(
        title = title.resolve(locale),
        description = description.resolve(locale),
        color = color,
        fields = statFields + scoreField,
    )
}

private fun scoreText(
    locale: BotLocale,
    playerOneName: String,
    playerTwoName: String,
    p1Score: Int,
    p2Score: Int,
): String = when {
    p1Score > p2Score ->
        DuelMessages.scoreLine(playerOneName, p1Score, p2Score).resolve(locale)

    p2Score > p1Score ->
        DuelMessages.scoreLine(playerTwoName, p2Score, p1Score).resolve(locale)

    else ->
        DuelMessages.tiedScore(p1Score).resolve(locale)
}

private data class StatComparison(
    val stat: DuelStat,
    val p1Value: Double,
    val p2Value: Double,
) {
    fun toField(locale: BotLocale, playerOneName: String, playerTwoName: String): Embed.Field {
        val p1Formatted = formatStat(stat.isDecimal, p1Value, locale)
        val p2Formatted = formatStat(stat.isDecimal, p2Value, locale)
        val cmp = stat.comparator.compare(p1Value, p2Value)
        val value = when {
            cmp > 0 ->
                "$GREEN_CIRCLE **$playerOneName**: $p1Formatted  |  $RED_CIRCLE $playerTwoName: $p2Formatted"

            cmp < 0 ->
                "$RED_CIRCLE $playerOneName: $p1Formatted  |  $GREEN_CIRCLE **$playerTwoName**: $p2Formatted"

            else ->
                "$WHITE_CIRCLE $playerOneName: $p1Formatted  |  $WHITE_CIRCLE $playerTwoName: $p2Formatted"
        }
        return Embed.Field(
            name = stat.label.resolve(locale),
            value = value,
        )
    }
}

private fun formatStat(isDecimal: Boolean, value: Double, locale: BotLocale): String {
    val javaLocale = when (locale) {
        BotLocale.EN_US -> Locale.US
        BotLocale.PT_BR -> Locale.of("pt", "BR")
    }
    return if (isDecimal) {
        String.format(javaLocale, "%,.2f", value)
    } else {
        String.format(javaLocale, "%,d", value.toInt())
    }
}
