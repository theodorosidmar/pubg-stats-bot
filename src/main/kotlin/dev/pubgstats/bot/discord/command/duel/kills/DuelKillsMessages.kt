package dev.pubgstats.bot.discord.command.duel.kills

import dev.pubgstats.bot.discord.command.Localized

object DuelKillsMessages {
    fun title(playerOne: String, playerTwo: String): Localized<String> = Localized(
        enUs = "$playerOne vs $playerTwo",
        ptBr = "$playerOne vs $playerTwo",
    )

    fun description(gameMode: String): Localized<String> = Localized(
        enUs = "KILLS DUEL AT $gameMode",
        ptBr = "DUELO DE MORTES NO $gameMode",
    )

    fun statLabel(stat: DuelKillsStat): Localized<String> = when (stat) {
        DuelKillsStat.DAMAGE_DEALT -> Localized(enUs = "Damage dealt", ptBr = "Dano causado")
        DuelKillsStat.HEADSHOT_KILLS -> Localized(enUs = "Headshot kills", ptBr = "Mortes por headshot")
        DuelKillsStat.KILLS -> Localized(enUs = "Kills", ptBr = "Mortes")
        DuelKillsStat.LONGEST_KILL -> Localized(enUs = "Longest kill", ptBr = "Morte mais longe")
        DuelKillsStat.ROAD_KILLS -> Localized(enUs = "Road kills", ptBr = "Mortes por atropelamento")
        DuelKillsStat.ROUND_MOST_KILLS -> Localized(enUs = "Round most kills", ptBr = "Mais mortes numa mesma partida")
    }

    fun scoreLine(winner: String, winnerScore: Int, loserScore: Int): Localized<String> = Localized(
        enUs = "🏆 **$winner** wins! ($winnerScore x $loserScore)",
        ptBr = "🏆 **$winner** venceu! ($winnerScore x $loserScore)",
    )

    fun tiedScore(score: Int): Localized<String> = Localized(
        enUs = "It's a tie! ($score x $score)",
        ptBr = "Empate! ($score x $score)",
    )
}
