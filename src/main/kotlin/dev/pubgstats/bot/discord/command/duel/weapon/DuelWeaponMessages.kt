package dev.pubgstats.bot.discord.command.duel.weapon

import dev.pubgstats.bot.discord.command.Localized

object DuelWeaponMessages {

    fun title(playerOne: String, playerTwo: String): Localized<String> = Localized(
        enUs = "$playerOne vs $playerTwo",
        ptBr = "$playerOne vs $playerTwo",
    )

    fun description(weapon: String): Localized<String> = Localized(
        enUs = "${weapon.uppercase()} DUEL",
        ptBr = "DUELO DE ${weapon.uppercase()}",
    )

    fun statLabel(stat: DuelWeaponStat): Localized<String> = when (stat) {
        DuelWeaponStat.XP_TOTAL -> Localized(enUs = "Total XP", ptBr = "XP Total")

        DuelWeaponStat.CURRENT_LEVEL -> Localized(enUs = "Level", ptBr = "Nível")

        DuelWeaponStat.CURRENT_TIER -> Localized(enUs = "Tier", ptBr = "Tier")

        DuelWeaponStat.MOST_DAMAGE_IN_GAME -> Localized(
            enUs = "Most Damage in a Game",
            ptBr = "Maior Dano em uma Partida",
        )

        DuelWeaponStat.TOTAL_DAMAGE -> Localized(enUs = "Total Damage", ptBr = "Dano Total")

        DuelWeaponStat.MOST_HEADSHOTS_IN_GAME -> Localized(
            enUs = "Most Headshots in a Game",
            ptBr = "Mais Headshots em uma Partida",
        )

        DuelWeaponStat.TOTAL_HEADSHOTS -> Localized(enUs = "Total Headshots", ptBr = "Headshots Totais")

        DuelWeaponStat.KILLS -> Localized(enUs = "Kills", ptBr = "Kills")

        DuelWeaponStat.MOST_KILLS_IN_GAME -> Localized(
            enUs = "Most Kills in a Game",
            ptBr = "Mais Kills em uma Partida",
        )
    }

    fun scoreLine(winner: String, winnerScore: Int, loserScore: Int): Localized<String> = Localized(
        enUs = "🏆 **$winner** wins! ($winnerScore x $loserScore)",
        ptBr = "🏆 **$winner** venceu! ($winnerScore x $loserScore)",
    )

    fun tiedScore(score: Int): Localized<String> = Localized(
        enUs = "It's a tie! ($score x $score)",
        ptBr = "Empate! ($score x $score)",
    )

    fun weaponNotFound(weapon: Weapon): Localized<String> = Localized(
        enUs = "Weapon \"${weapon.label}\" not found.",
        ptBr = "Arma \"${weapon.label}\" não encontrada.",
    )

    fun noStats(player: String, weapon: Weapon): Localized<String> = Localized(
        enUs = "**$player** has no stats for \"${weapon.label}\".",
        ptBr = "**$player** não possui estatísticas para \"${weapon.label}\".",
    )
}
