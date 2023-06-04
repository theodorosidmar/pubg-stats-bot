package com.github.theodorosidmar.pubgstats.bot

@JvmInline
value class Command(private val withPrefix: String) {
    companion object {
        private val allowedCommands = setOf("solo", "duo", "squad")
        const val prefix = "!"
    }

    init {
        require(name in allowedCommands) { "Invalid command $name" }
    }

    val name: String get() = withPrefix.removePrefix(prefix)
}
