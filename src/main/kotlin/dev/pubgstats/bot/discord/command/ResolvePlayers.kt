package dev.pubgstats.bot.discord.command

import dev.pubgkt.PubgApi
import dev.pubgkt.players.Player
import dev.pubgkt.players.getPlayersByNames
import dev.pubgstats.bot.cache.Cache

suspend fun PubgApi.resolvePlayers(vararg names: String, cache: Cache<String, Player> = Caches.players): List<Player> {
    val cached = names.mapNotNull { cache[it] }
    if (cached.size == names.size) {
        return cached
    }
    val playersByName = getPlayersByNames(*names)
        .associateBy { it.name.lowercase() }
    playersByName.values.forEach { cache.put(it.name, it) }
    return names.map { playersByName.getValue(it.lowercase()) }
}
