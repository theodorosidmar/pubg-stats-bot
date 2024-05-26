package dev.pubgstats.bot.discord

import dev.kord.core.entity.Message
import dev.pubgstats.bot.Command
import dev.pubgstats.bot.LifetimeCommand
import dev.pubgstats.bot.X1Command
import pubgkt.GameMode

internal fun Message.isValid(): Boolean =
    content.startsWith("!") && !author?.isBot!!

internal fun Message.toCommand(): Command {
    val (command, arguments) = content
        .removePrefix("!")
        .split(' ')
        .let {
            it.first() to it.drop(1)
        }
    return if (command.lowercase() == "x1") {
        val (playerOne, playerTwo, gameMode) = arguments
        X1Command(
            playerOne = playerOne,
            playerTwo = playerTwo,
            gameMode = GameMode.entries.find { it.name.uppercase() == gameMode.uppercase() }!!
        )
    } else {
        val (player, gameMode) = arguments
        LifetimeCommand(
            player = player,
            gameMode = GameMode.entries.find { it.name.uppercase() == gameMode.uppercase() }!!
        )
    }
}
