package dev.pubgstats.bot.discord.command

/** Common contract for anything the dispatcher can route a command interaction to. */
sealed interface CommandHandler {
    val name: String
    val description: Localized<String>
    val visibility: Visibility

    suspend fun handle(ctx: CommandContext)
}
