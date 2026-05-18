package dev.pubgstats.bot.discord.command

/**
 * Base class for slash command handlers.
 *
 * Subclasses declare their options and visibility, extract typed parameters
 * via [parseParams], and implement the command logic in [execute].
 * The framework guarantees that [parseParams] runs before [execute] on every invocation.
 *
 * @param P the typed parameters extracted from the interaction options.
 */
abstract class RegularCommandHandler<P> : CommandHandler {
    abstract override val name: String
    abstract override val description: Localized<String>
    open val options: List<CommandOption> = emptyList()
    override val visibility: Visibility = Visibility.PUBLIC

    protected abstract fun parseParams(ctx: CommandContext): P
    protected abstract suspend fun execute(ctx: CommandContext, params: P)

    override suspend fun handle(ctx: CommandContext) {
        val params = parseParams(ctx)
        execute(ctx, params)
    }
}
