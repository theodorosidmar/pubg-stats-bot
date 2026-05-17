package dev.pubgstats.bot.discord.command

data class FakeCommandContext(
    override val locale: BotLocale = BotLocale.EN_US,
    override val strings: Map<String, String> = emptyMap(),
    override val integers: Map<String, Long> = emptyMap(),
    override val booleans: Map<String, Boolean> = emptyMap(),
) : CommandContext {
    val responses = mutableListOf<String>()
    val embeds = mutableListOf<Localized<Embed>>()

    override suspend fun respond(content: String) {
        responses += content
    }

    override suspend fun respondEmbed(embed: Localized<Embed>) {
        embeds += embed
    }
}
