package dev.pubgstats.bot.discord.command.duel

import dev.pubgstats.bot.discord.command.Localized

object DuelMessages {
    fun title(playerOne: String, playerTwo: String): Localized<String> = Localized(
        enUs = "$playerOne vs $playerTwo",
        ptBr = "$playerOne vs $playerTwo",
    )

    fun scoreLine(winner: String, winnerScore: Int, loserScore: Int): Localized<String> = Localized(
        enUs = "🏆 **$winner** wins! ($winnerScore x $loserScore)",
        ptBr = "🏆 **$winner** venceu! ($winnerScore x $loserScore)",
    )

    fun tiedScore(score: Int): Localized<String> = Localized(
        enUs = "It's a tie! ($score x $score)",
        ptBr = "Empate! ($score x $score)",
    )
}
