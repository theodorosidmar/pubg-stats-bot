package dev.pubgstats.bot.discord

sealed interface Property {
    val argKey: String
    val systemPropertyKey: String
    val envVarKey: String

    val argPrefix: String
        get() = "--$argKey="
}

sealed interface UserInputProperty<T : Any> : Property {
    fun parse(value: String): T
}

sealed interface DefaultedProperty<T : Any> : Property {
    val default: T
    fun parse(value: String): T
}

data object DiscordTokenProperty : UserInputProperty<String> {
    override val argKey: String = "discord-token"
    override val systemPropertyKey: String = "discord.token"
    override val envVarKey: String = "DISCORD_TOKEN"

    override fun parse(value: String): String = value
}

data object PubgApiKeyProperty : UserInputProperty<String> {
    override val argKey: String = "pubg-api-key"
    override val systemPropertyKey: String = "pubg.api.key"
    override val envVarKey: String = "PUBG_API_KEY"

    override fun parse(value: String): String = value
}

data object GatewayWorkersProperty : DefaultedProperty<Int> {
    override val argKey = "gateway-workers"
    override val systemPropertyKey = "gateway.workers"
    override val envVarKey = "GATEWAY_WORKERS"
    override val default: Int = 2

    override fun parse(value: String): Int = value.toInt()
}

fun <T : Any> resolveProperty(args: Array<String>, property: UserInputProperty<T>, onNull: () -> Nothing): T {
    val raw = resolveRaw(args, property) ?: onNull()
    return property.parse(raw)
}

fun <T : Any> resolveProperty(args: Array<String>, property: DefaultedProperty<T>): T {
    val raw = resolveRaw(args, property) ?: return property.default
    return property.parse(raw)
}

private fun resolveRaw(args: Array<String>, property: Property): String? =
    args.firstOrNull { it.startsWith(property.argPrefix) }
        ?.removePrefix(property.argPrefix)
        ?: System.getProperty(property.systemPropertyKey)
        ?: System.getenv(property.envVarKey)
