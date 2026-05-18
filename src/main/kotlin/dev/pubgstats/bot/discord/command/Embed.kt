package dev.pubgstats.bot.discord.command

private const val ICON_URL =
    "https://raw.githubusercontent.com/theodorosidmar/pubg-stats-bot/main/assets/icon.png"
private val AUTHOR = Embed.Author(name = "PUBG Stats Bot", iconUrl = ICON_URL)

/**
 * Library-agnostic representation of a Discord rich embed (the colored card
 * with title, description, and fields). The bridge layer converts this to
 * the underlying Discord library's embed type before sending.
 */
data class Embed(
    var title: String? = null,
    var description: String? = null,
    var color: Int? = null,
    val author: Author? = AUTHOR,
    val fields: List<Field> = emptyList(),
) {
    data class Author(val name: String, val iconUrl: String? = null)
    data class Field(val name: String, val value: String, val inline: Boolean = false)
}
