package com.github.theodorosidmar.pubgstats.bot

interface PubgStatsBot {
    suspend fun getLifetimeStats(player: String, command: Command): String
}
