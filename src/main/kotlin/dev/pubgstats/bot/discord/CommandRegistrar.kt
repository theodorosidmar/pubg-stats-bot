package dev.pubgstats.bot.discord

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.RootInputChatBuilder
import dev.kord.rest.builder.interaction.boolean
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.subCommand
import dev.kord.rest.builder.interaction.user
import dev.pubgstats.bot.discord.command.CommandHandler
import dev.pubgstats.bot.discord.command.CommandOption
import dev.pubgstats.bot.discord.command.ParentCommandHandler
import dev.pubgstats.bot.discord.command.RegularCommandHandler
import kotlinx.coroutines.flow.collect
import org.slf4j.LoggerFactory
import dev.kord.common.Locale as KordLocale

class CommandRegistrar(private val handlers: Map<String, CommandHandler>) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun register(kord: Kord) {
        kord.createGlobalApplicationCommands {
            handlers.values.forEach { handler ->
                logger.debug("Registering slash command: /{} ({})", handler.name, handler.description.enUs)
                input(handler.name, handler.description.enUs) {
                    description(KordLocale.PORTUGUESE_BRAZIL, handler.description.ptBr)
                    when (handler) {
                        is RegularCommandHandler<*> -> buildOptions(handler.options)
                        is ParentCommandHandler -> buildSubCommands(handler.childHandlers)
                    }
                }
            }
        }.collect()
        logger.info("Registered {} slash command(s)", handlers.size)
    }
}

private fun RootInputChatBuilder.buildSubCommands(subCommands: List<RegularCommandHandler<*>>) {
    subCommands.forEach { sub ->
        subCommand(sub.name, sub.description.enUs) {
            description(KordLocale.PORTUGUESE_BRAZIL, sub.description.ptBr)
            buildOptions(sub.options)
        }
    }
}

private fun BaseInputChatBuilder.buildOptions(options: List<CommandOption>) {
    options.forEach { option ->
        when (option) {
            is CommandOption.StringOption -> string(option.name, option.description.enUs) {
                required = option.required
                description(KordLocale.PORTUGUESE_BRAZIL, option.description.ptBr)
                option.choices.forEach { choice(it.label, it.value) }
            }

            is CommandOption.IntegerOption -> integer(option.name, option.description.enUs) {
                required = option.required
                description(KordLocale.PORTUGUESE_BRAZIL, option.description.ptBr)
            }

            is CommandOption.BooleanOption -> boolean(option.name, option.description.enUs) {
                required = option.required
                description(KordLocale.PORTUGUESE_BRAZIL, option.description.ptBr)
            }

            is CommandOption.UserOption -> user(option.name, option.description.enUs) {
                required = option.required
                description(KordLocale.PORTUGUESE_BRAZIL, option.description.ptBr)
            }
        }
    }
}
