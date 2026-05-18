package dev.pubgstats.bot.discord.command

class PingHandler : RegularCommandHandler<Unit>() {
    override val name = "ping"
    override val description = Localized(enUs = "Replies with pong", ptBr = "Responde com pong")

    override fun parseParams(ctx: CommandContext) = Unit

    override suspend fun execute(ctx: CommandContext, params: Unit) {
        ctx.respond("pong")
    }
}

class DuelCommandHandler : ParentCommandHandler() {
    override val name: String = "duel"

    override val description: Localized<String> = Localized(
        enUs = "Duel with your friends or other players to see who is the best",
        ptBr = "Compare suas estatísticas com as de outro jogador para ver quem é o melhor",
    )

    override val childHandlers: Map<String, RegularCommandHandler<*>> = buildMap {
        DuelGunCommandHandler().let { put(it.name, it) }
    }
}

class DuelGunCommandHandler : RegularCommandHandler<DuelGunCommandHandler.Params>() {
    override val name: String = "gun"
    override val description: Localized<String> = Localized(
        enUs = "Compare gun stats between two players",
        ptBr = "Compare estatísticas de arma entre dois jogadores",
    )

    data class Params(
        val playerOne: String,
        val playerTwo: String,
        val gun: String,
    )

    override val options: List<CommandOption> = listOf(
        CommandOption.StringOption(
            name = "player-one",
            description = Localized(
                enUs = "Player one",
                ptBr = "Jogador um",
            ),
            required = true,
        ),
        CommandOption.StringOption(
            name = "player-two",
            description = Localized(
                enUs = "Player two",
                ptBr = "Jogador dois",
            ),
            required = true,
        ),
        CommandOption.StringOption(
            name = "gun",
            description = Localized(
                enUs = "Gun to compare",
                ptBr = "Arma para comparar",
            ),
            required = true,
        ),
    )

    override fun parseParams(ctx: CommandContext): Params = Params(
        playerOne = ctx.requireString("player-one"),
        playerTwo = ctx.requireString("player-two"),
        gun = ctx.requireString("gun"),
    )

    override suspend fun execute(ctx: CommandContext, params: Params) {
        println("Params received $params")
        println("Context is $ctx")
        ctx.respondEmbed(Localized<Embed>(
            enUs = Embed(
                title = "Duel: ${params.playerOne} vs ${params.playerTwo}",
                description = "Comparing gun stats for ${params.gun}",
                color = 0xFF0000,
                fields = mutableListOf(
                    Embed.Field(
                        name = "Player One",
                        value = "Stats for ${params.playerOne}",
                    ),
                    Embed.Field(
                        name = "Player Two",
                        value = "Stats for ${params.playerTwo}",
                    ),
                ),
            ),
            ptBr = Embed(
                title = "Duelo: ${params.playerOne} vs ${params.playerTwo}",
                description = "Comparando estatísticas de arma para ${params.gun}",
                color = 0xFF0000,
                fields = mutableListOf(
                    Embed.Field(
                        name = "Player One",
                        value = "Stats for ${params.playerOne}",
                    ),
                    Embed.Field(
                        name = "Player Two",
                        value = "Stats for ${params.playerTwo}",
                    ),
                ),
            ),
        ))
    }
}
