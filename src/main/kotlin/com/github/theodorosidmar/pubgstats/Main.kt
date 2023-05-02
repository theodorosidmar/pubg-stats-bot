package com.github.theodorosidmar.pubgstats

suspend fun main() {
    val token = System.getenv("DISCORD_TOKEN") ?: error("Discord bot token required")
    PubgStatsBot(token).init()
}
