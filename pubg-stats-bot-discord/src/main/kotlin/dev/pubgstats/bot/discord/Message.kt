package dev.pubgstats.bot.discord

import dev.kord.core.entity.Message
import dev.pubgstats.bot.Command

internal fun Message.isValid(): Boolean =
    this.content.startsWith(Command.prefix) && !this.author?.isBot!!

internal fun Message.toCommand(): Command {
    val (first) = this.content.split(' ')
    return Command(first)
}
