package dev.pubgstats.bot.discord.command

/**
 * Describes a slash command option that is registered with Discord
 * and used to extract values from the interaction at dispatch time.
 *
 * Each variant maps to a Discord option type (string, integer, boolean, user).
 */
sealed interface CommandOption {
    val name: String
    val description: Localized<String>
    val required: Boolean

    data class StringOption(
        override val name: String,
        override val description: Localized<String>,
        override val required: Boolean = false,
        val choices: List<Choice> = emptyList(),
    ) : CommandOption {
        data class Choice(val label: String, val value: String)
    }

    data class IntegerOption(
        override val name: String,
        override val description: Localized<String>,
        override val required: Boolean = false,
    ) : CommandOption

    data class BooleanOption(
        override val name: String,
        override val description: Localized<String>,
        override val required: Boolean = false,
    ) : CommandOption

    data class UserOption(
        override val name: String,
        override val description: Localized<String>,
        override val required: Boolean = false,
    ) : CommandOption
}
