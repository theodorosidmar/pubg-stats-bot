package dev.pubgstats.bot.discord.command.duel

import dev.pubgkt.PubgApi
import dev.pubgkt.players.Player
import dev.pubgstats.bot.discord.command.RegularCommandHandler
import dev.pubgstats.bot.discord.command.resolvePlayers

abstract class DuelSubCommandHandler<P : DuelParams>(
    protected val pubgApi: PubgApi,
) : RegularCommandHandler<P>() {

    protected suspend fun resolveDuelPlayers(params: P): Pair<Player, Player> {
        val (p1, p2) = pubgApi.resolvePlayers(params.playerOne, params.playerTwo)
        return p1 to p2
    }
}
