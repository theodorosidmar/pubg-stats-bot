package dev.pubgstats.bot.discord

suspend fun main() {
    val token = System.getenv("DISCORD_TOKEN") ?: error("Discord bot token required")
    PubgStatsBotDiscord(token).init()
}
