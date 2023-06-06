package com.github.theodorosidmar.pubgstats.bot.discord

import com.github.theodorosidmar.pubgstats.bot.Command
import dev.kord.core.entity.Message

internal fun Message.isValid(): Boolean =
    this.content.startsWith(Command.prefix) && !this.author?.isBot!!

internal fun Message.toCommandAndPlayerName(): Pair<Command, String> {
    val (first, second) = this.content.split(' ', limit = 2)
    return Command(first) to second
}
