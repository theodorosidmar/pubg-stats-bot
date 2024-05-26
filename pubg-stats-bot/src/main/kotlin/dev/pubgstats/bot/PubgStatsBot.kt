package dev.pubgstats.bot

interface PubgStatsBot {
    suspend fun getLifetimeStats(command: LifetimeCommand): String
    suspend fun x1(command: X1Command): String
}
