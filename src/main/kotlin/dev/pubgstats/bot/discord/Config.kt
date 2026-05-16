package dev.pubgstats.bot.discord

data class Config(
    val discordToken: String,
    val pubgApiKey: String,
    val gatewayWorkers: Int,
)
