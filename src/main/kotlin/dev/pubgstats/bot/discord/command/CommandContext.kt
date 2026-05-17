package dev.pubgstats.bot.discord.command

/**
 * Abstraction over a slash command interaction, providing access to
 * the user-supplied options and the ability to send a response.
 *
 * Implementations bridge this interface to a specific Discord library.
 * Handlers depend only on this contract, keeping them testable in isolation.
 */
interface CommandContext {
    val locale: BotLocale
    val strings: Map<String, String>
    val integers: Map<String, Long>
    val booleans: Map<String, Boolean>

    fun requireString(name: String): String = checkNotNull(strings[name]) { "Missing required string option: $name" }

    fun requireInteger(name: String): Long = checkNotNull(integers[name]) { "Missing required integer option: $name" }

    fun requireBoolean(name: String): Boolean =
        checkNotNull(booleans[name]) { "Missing required boolean option: $name" }

    suspend fun respond(content: String)
    suspend fun respond(content: Localized<String>) = respond(content.resolve(locale))
    suspend fun respondEmbed(embed: Localized<Embed>)
}
