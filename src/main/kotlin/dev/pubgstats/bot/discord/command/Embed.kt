package dev.pubgstats.bot.discord.command

/**
 * Library-agnostic representation of a Discord rich embed (the colored card
 * with title, description, and fields). The bridge layer converts this to
 * the underlying Discord library's embed type before sending.
 */
data class Embed(
    var title: String? = null,
    var description: String? = null,
    var color: Int? = null,
    val fields: MutableList<Field> = mutableListOf(),
) {
    data class Field(val name: String, val value: String, val inline: Boolean = false)

    fun field(name: String, value: String, inline: Boolean = false) {
        fields += Field(name, value, inline)
    }
}
