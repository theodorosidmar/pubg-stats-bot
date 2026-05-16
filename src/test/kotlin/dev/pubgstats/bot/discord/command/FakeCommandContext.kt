package dev.pubgstats.bot.discord.command

class FakeCommandContext(
    override val strings: Map<String, String> = emptyMap(),
    override val integers: Map<String, Long> = emptyMap(),
    override val booleans: Map<String, Boolean> = emptyMap(),
) : CommandContext {
    val responses = mutableListOf<String>()
    val embeds = mutableListOf<Embed>()

    override suspend fun respond(content: String) {
        responses += content
    }

    override suspend fun respond(embed: Embed) {
        embeds += embed
    }
}
