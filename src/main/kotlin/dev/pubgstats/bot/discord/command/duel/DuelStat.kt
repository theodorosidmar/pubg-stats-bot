package dev.pubgstats.bot.discord.command.duel

import dev.pubgstats.bot.discord.command.Localized

interface DuelStat {
    val label: Localized<String>
    val isDecimal: Boolean
    val comparator: Comparator<Double>
        get() = naturalOrder()
}
