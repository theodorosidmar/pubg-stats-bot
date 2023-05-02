package com.github.theodorosidmar.pubgstats

@JvmInline
value class Command(val withPrefix: String) {
    companion object {
        private val allowedCommands: Set<String> = setOf("solo", "duo", "squad")
        const val prefix = "!"
    }

    init {
        require(name in allowedCommands) { "Invalid command $name" }
    }

    val name: String get() = withPrefix.removePrefix(prefix)
}
