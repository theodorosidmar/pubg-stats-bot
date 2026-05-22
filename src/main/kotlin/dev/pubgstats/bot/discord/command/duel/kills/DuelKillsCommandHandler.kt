package dev.pubgstats.bot.discord.command.duel.kills

import dev.pubgkt.GameMode
import dev.pubgkt.PubgApi
import dev.pubgkt.stats.lifetime.getLifetimeStatsByGameModeAndPlayers
import dev.pubgstats.bot.discord.command.CommandContext
import dev.pubgstats.bot.discord.command.CommandOption
import dev.pubgstats.bot.discord.command.Localized
import dev.pubgstats.bot.discord.command.PLAYER_ONE_OPTION
import dev.pubgstats.bot.discord.command.PLAYER_TWO_OPTION
import dev.pubgstats.bot.discord.command.duel.DuelParams
import dev.pubgstats.bot.discord.command.duel.DuelSubCommandHandler

class DuelKillsCommandHandler(
    pubgApi: PubgApi,
) : DuelSubCommandHandler<DuelKillsCommandHandler.Params>(pubgApi) {
    override val name: String = "kills"
    override val description: Localized<String> = Localized(
        enUs = "Compare kills stats between two players",
        ptBr = "Compare estatísticas de mortes entre dois jogadores",
    )

    data class Params(
        override val playerOne: String,
        override val playerTwo: String,
        val gameMode: GameMode,
    ) : DuelParams

    override val options: List<CommandOption> = listOf(
        PLAYER_ONE_OPTION,
        PLAYER_TWO_OPTION,
        CommandOption.StringOption(
            name = "game-mode",
            description = Localized(
                enUs = "The game mode",
                ptBr = "Modo de jogo",
            ),
            required = true,
            choices = GameMode.entries.map {
                CommandOption.StringOption.Choice(
                    label = it.name
                        .lowercase()
                        .replace(oldValue = "_", newValue = "-"),
                    value = it.name,
                )
            },
        ),
    )

    override fun parseParams(ctx: CommandContext): Params = Params(
        playerOne = ctx.requirePlayerOne(),
        playerTwo = ctx.requirePlayerTwo(),
        gameMode = GameMode.valueOf(ctx.requireString("game-mode")),
    )

    override suspend fun execute(ctx: CommandContext, params: Params) {
        val (playerOne, playerTwo) = resolveDuelPlayers(params)

        val (p1Stats, p2Stats) =
            pubgApi
                .getLifetimeStatsByGameModeAndPlayers(
                    gameMode = params.gameMode,
                    accountIds = listOf(playerOne.id, playerTwo.id),
                )
                .associateBy { it.playerId }
                .let { it.getValue(playerOne.id) to it.getValue(playerTwo.id) }

        ctx.respondEmbed(
            buildDuelKillsEmbed(
                playerOneName = playerOne.name,
                playerTwoName = playerTwo.name,
                playerOneStats = p1Stats,
                playerTwoStats = p2Stats,
            ),
        )
    }
}
