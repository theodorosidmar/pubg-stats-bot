package dev.pubgstats.bot

import pubgkt.GameMode

sealed class Command

data class X1Command(
    val playerOne: String,
    val playerTwo: String,
    val gameMode: GameMode,
) : Command()

data class LifetimeCommand(
    val player: String,
    val gameMode: GameMode,
) : Command()
