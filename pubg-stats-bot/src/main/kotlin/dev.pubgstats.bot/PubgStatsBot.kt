package dev.pubgstats.bot

interface PubgStatsBot {
    suspend fun getLifetimeStats(player: String, command: Command): String
    suspend fun x1(playerOne: String, playerTwo: String, command: Command): String
}
