package dev.pubgstats.bot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import pubgkt.GameMode
import pubgkt.PubgApi
import pubgkt.Stats

abstract class PubgStatsBot(private val pubgApi: PubgApi) {
    abstract val prefix: String?
    abstract suspend fun outputLifetime(command: LifetimeCommand, stats: Stats): String
    abstract suspend fun outputX1(command: X1Command, playerOneStats: Stats, playerTwoStats: Stats): String

    private suspend fun getLifetime(command: LifetimeCommand): String =
        pubgApi.getLifetimeStats(command.player, command.gameMode)
            .getOrThrow()
            .let { outputLifetime(command, it) }

    private suspend fun getX1(command: X1Command): String {
        val (playerOneStats, playerTwoStats) =
            withContext(Dispatchers.IO) {
                awaitAll(
                    async { pubgApi.getLifetimeStats(command.playerOne, command.gameMode) },
                    async { pubgApi.getLifetimeStats(command.playerTwo, command.gameMode) }
                )
            }
        return outputX1(
            command,
            playerOneStats.getOrThrow(),
            playerTwoStats.getOrThrow(),
        )
    }

    suspend fun processMessage(commandString: String): String =
        commandString.toCommand().let {
            when (it) {
                is X1Command -> getX1(it)
                is LifetimeCommand -> getLifetime(it)
            }
        }

    private fun String.toCommand(): Command {
        val (command, arguments) = this
            .removePrefix(prefix.orEmpty())
            .split(' ')
            .let {
                it.first() to it.drop(1)
            }
        return if (command.lowercase() == "x1") {
            val (playerOne, playerTwo, gameMode) = arguments
            X1Command(
                playerOne = playerOne,
                playerTwo = playerTwo,
                gameMode = GameMode.entries.find { it.name.uppercase() == gameMode.uppercase() }!!,
            )
        } else {
            val (player, gameMode) = arguments
            LifetimeCommand(
                player = player,
                gameMode = GameMode.entries.find { it.name.uppercase() == gameMode.uppercase() }!!,
            )
        }
    }
}
