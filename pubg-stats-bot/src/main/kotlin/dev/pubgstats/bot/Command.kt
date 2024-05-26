package dev.pubgstats.bot

import pubgkt.GameMode

sealed class Command

class X1Command(
    val playerOne: String,
    val playerTwo: String,
    val gameMode: GameMode,
) : Command()

class LifetimeCommand(
    val player: String,
    val gameMode: GameMode,
) : Command()
