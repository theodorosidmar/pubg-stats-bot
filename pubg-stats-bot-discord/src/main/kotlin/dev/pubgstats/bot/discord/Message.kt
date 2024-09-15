package dev.pubgstats.bot.discord

import dev.kord.core.entity.Message

internal fun Message.isValid(): Boolean =
    content.startsWith("!") && !author?.isBot!!

internal fun Message.isNotValid(): Boolean =
    !this.isValid()
