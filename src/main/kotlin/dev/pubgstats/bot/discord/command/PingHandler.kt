package dev.pubgstats.bot.discord.command

class PingHandler : RegularCommandHandler<Unit>() {
    override val name = "ping"
    override val description = Localized(enUs = "Replies with pong", ptBr = "Responde com pong")

    override fun parseParams(ctx: CommandContext) = Unit

    override suspend fun execute(ctx: CommandContext, params: Unit) {
        ctx.respond("pong")
    }
}
