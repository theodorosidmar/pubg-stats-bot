package dev.pubgstats.bot.discord.command.duel.weapon

import dev.pubgstats.bot.discord.command.Localized
import dev.pubgstats.bot.discord.command.duel.DuelStat

enum class DuelWeaponStat(
    override val label: Localized<String>,
    override val isDecimal: Boolean = false,
) : DuelStat {
    XP_TOTAL(Localized(enUs = "Total XP", ptBr = "XP Total")),
    CURRENT_LEVEL(Localized(enUs = "Level", ptBr = "Nível")),
    CURRENT_TIER(Localized(enUs = "Tier", ptBr = "Tier")),
    MOST_DAMAGE_IN_GAME(
        Localized(enUs = "Most Damage in a Game", ptBr = "Maior Dano em uma Partida"),
        isDecimal = true,
    ),
    TOTAL_DAMAGE(Localized(enUs = "Total Damage", ptBr = "Dano Total"), isDecimal = true),
    MOST_HEADSHOTS_IN_GAME(Localized(enUs = "Most Headshots in a Game", ptBr = "Mais Headshots em uma Partida")),
    TOTAL_HEADSHOTS(Localized(enUs = "Total Headshots", ptBr = "Headshots Totais")),
    KILLS(Localized(enUs = "Kills", ptBr = "Kills")),
    MOST_KILLS_IN_GAME(Localized(enUs = "Most Kills in a Game", ptBr = "Mais Kills em uma Partida")),
}
