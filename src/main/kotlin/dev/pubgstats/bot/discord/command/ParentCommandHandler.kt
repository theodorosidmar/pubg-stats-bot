package dev.pubgstats.bot.discord.command

/**
 * A command that delegates to subcommands.
 *
 * Subclasses declare [childHandlers] — each is a regular [RegularCommandHandler] registered
 * as a Discord subcommand under this parent's [name].
 */
abstract class ParentCommandHandler : CommandHandler {
    abstract override val name: String
    abstract override val description: Localized<String>
    override val visibility: Visibility = Visibility.PUBLIC
    abstract val childHandlers: List<RegularCommandHandler<*>>

    override suspend fun handle(ctx: CommandContext) {
        val child = childHandlers.first { it.name == ctx.subCommandName }
        child.handle(ctx)
    }
}
