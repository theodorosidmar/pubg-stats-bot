package dev.pubgstats.bot.discord.command.duel.kills

import dev.pubgstats.bot.discord.command.Localized
import dev.pubgstats.bot.discord.command.duel.DuelStat

enum class DuelKillsStat(
    override val label: Localized<String>,
    override val isDecimal: Boolean = false,
) : DuelStat {
    DAMAGE_DEALT(Localized(enUs = "Damage dealt", ptBr = "Dano causado"), isDecimal = true),
    HEADSHOT_KILLS(Localized(enUs = "Headshot kills", ptBr = "Mortes por headshot")),
    KILLS(Localized(enUs = "Kills", ptBr = "Mortes")),
    LONGEST_KILL(Localized(enUs = "Longest kill", ptBr = "Morte mais longe"), isDecimal = true),
    ROAD_KILLS(Localized(enUs = "Road kills", ptBr = "Mortes por atropelamento")),
    ROUND_MOST_KILLS(Localized(enUs = "Round most kills", ptBr = "Mais mortes numa mesma partida")),
}
