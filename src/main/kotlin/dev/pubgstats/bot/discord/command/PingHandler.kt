package dev.pubgstats.bot.discord.command

class PingHandler : CommandHandler<Unit>() {
    override val name = "ping"
    override val description = "Replies with pong"

    override fun parseParams(ctx: CommandContext) = Unit

    override suspend fun execute(ctx: CommandContext, params: Unit) {
        ctx.respond("pong")
    }
}
