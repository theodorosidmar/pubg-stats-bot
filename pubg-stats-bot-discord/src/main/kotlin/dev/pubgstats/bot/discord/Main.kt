package dev.pubgstats.bot.discord

suspend fun main() {
    val token = System.getenv("DISCORD_TOKEN") ?: error("Discord bot token required")
    val applicationId = System.getenv("DISCORD_APPLICATION_ID") ?: error("Discord application id required")
    PubgStatsBotDiscord(token, applicationId).init()
}
