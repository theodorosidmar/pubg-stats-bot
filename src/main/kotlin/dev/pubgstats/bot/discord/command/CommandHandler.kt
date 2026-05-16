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
abstract class CommandHandler<P> {
    abstract val name: String
    abstract val description: Localized<String>
    open val options: List<CommandOption> = emptyList()
    open val visibility: Visibility = Visibility.PUBLIC

    protected abstract fun parseParams(ctx: CommandContext): P
    protected abstract suspend fun execute(ctx: CommandContext, params: P)

    suspend fun handle(ctx: CommandContext) {
        val params = parseParams(ctx)
        execute(ctx, params)
    }
}
