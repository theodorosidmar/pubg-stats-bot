package dev.pubgstats.bot.discord

import kotlin.system.exitProcess

suspend fun main(args: Array<String>) {
    if ("--debug" in args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")
    }
    val config = resolveConfig(args)
    Application(config).run()
}

private fun resolveConfig(args: Array<String>): Config = Config(
    discordToken = resolveProperty(args, DiscordTokenProperty) {
        System.err.println(
            "Discord bot token is required. " +
                "Provide via --discord-token=<value>, -Ddiscord.token=<value>, or DISCORD_TOKEN env var.",
        )
        exitProcess(1)
    },
    pubgApiKey = resolveProperty(args, PubgApiKeyProperty) {
        System.err.println(
            "PUBG API key is required. " +
                "Provide via --pubg-api-key=<value>, -Dpubg.api.key=<value>, or PUBG_API_KEY env var.",
        )
        exitProcess(1)
    },
    gatewayWorkers = resolveProperty(args, GatewayWorkersProperty),
)
