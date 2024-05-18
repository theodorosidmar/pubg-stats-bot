package dev.pubgstats.bot.discord

import dev.kord.core.entity.Message
import dev.pubgstats.bot.Command

internal fun Message.isValid(): Boolean =
    this.content.startsWith(Command.prefix) && !this.author?.isBot!!

internal fun Message.toCommandAndPlayerName(): Pair<Command, String> {
    val (first, second) = this.content.split(' ', limit = 2)
    return Command(first) to second
}
