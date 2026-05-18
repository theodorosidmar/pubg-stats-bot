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
    abstract val childHandlers: Map<String, RegularCommandHandler<*>>

    override suspend fun handle(ctx: CommandContext) {
        val childHandler = childHandlers[ctx.subCommandName]
        checkNotNull(childHandler)
        childHandler.handle(ctx)
    }
}
