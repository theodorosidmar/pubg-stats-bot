package dev.pubgstats.bot.discord

import dev.kord.common.Color
import dev.kord.common.Locale.Companion.PORTUGUESE_BRAZIL
import dev.kord.core.behavior.interaction.response.DeferredMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.rest.builder.message.EmbedBuilder
import dev.pubgstats.bot.discord.command.BotLocale
import dev.pubgstats.bot.discord.command.CommandContext
import dev.pubgstats.bot.discord.command.Embed
import dev.pubgstats.bot.discord.command.Localized
import dev.kord.common.Locale as KordLocale

class KordCommandContext(
    interaction: ChatInputCommandInteraction,
    private val deferred: DeferredMessageInteractionResponseBehavior,
) : CommandContext {
    override val locale: BotLocale = interaction.locale.toBotLocale()
    override val strings: Map<String, String> = interaction.command.strings
    override val integers: Map<String, Long> = interaction.command.integers
    override val booleans: Map<String, Boolean> = interaction.command.booleans
    override val subCommandName: String? = (interaction.command as? SubCommand)?.name

    override suspend fun respond(content: String) {
        deferred.respond { this.content = content }
    }

    override suspend fun respondEmbed(embed: Localized<Embed>) {
        deferred.respond { embeds = mutableListOf(embed.resolve(locale).toKord()) }
    }
}

private fun KordLocale?.toBotLocale(): BotLocale = when {
    this == PORTUGUESE_BRAZIL -> BotLocale.PT_BR
    else -> BotLocale.EN_US
}

private fun Embed.toKord(): EmbedBuilder = EmbedBuilder().apply {
    this@toKord.title?.let { title = it }
    this@toKord.description?.let { description = it }
    this@toKord.color?.let { color = Color(it) }
    this@toKord.fields.forEach { field ->
        field {
            name = field.name
            value = field.value
            inline = field.inline
        }
    }
}
