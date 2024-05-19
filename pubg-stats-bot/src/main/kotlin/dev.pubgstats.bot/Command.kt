package dev.pubgstats.bot

import pubgkt.GameMode

@JvmInline
value class Command(private val withPrefix: String) {
    companion object {
        private val allowedCommands = setOf("solo", "duo", "squad", "x1")
        const val prefix = "!"
    }

    init {
        require(name in allowedCommands) { "Invalid command $name" }
    }

    val name: String get() = withPrefix.removePrefix(prefix)

    fun toGameMode(): GameMode = when (name) {
        "solo" -> GameMode.SoloFpp
        "duo" -> GameMode.DuoFpp
        "squad" -> GameMode.SquadFpp
        else -> throw NotImplementedError()
    }
}
