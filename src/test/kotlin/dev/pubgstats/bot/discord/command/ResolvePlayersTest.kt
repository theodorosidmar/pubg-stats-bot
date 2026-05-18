package dev.pubgstats.bot.discord.command

import dev.pubgkt.PubgApi
import dev.pubgkt.players.BanType
import dev.pubgkt.players.Player
import dev.pubgstats.bot.cache.cache
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ResolvePlayersTest {

    private val cache = cache<String, Player> { maximumSize = 10 }
    private val pubgApi = PubgApi(apiKey = "unused")

    @Test
    fun `returns cached players when all are present`() = runTest {
        cache.put("Alice", player("Alice", "id-alice"))
        cache.put("Bob", player("Bob", "id-bob"))

        val result = pubgApi.resolvePlayers("Alice", "Bob", cache = cache)

        assertEquals("Alice", result[0].name)
        assertEquals("Bob", result[1].name)
    }

    @Test
    fun `returns players in input order`() = runTest {
        cache.put("Bob", player("Bob", "id-bob"))
        cache.put("Alice", player("Alice", "id-alice"))

        val result = pubgApi.resolvePlayers("Alice", "Bob", cache = cache)

        assertEquals("Alice", result[0].name)
        assertEquals("Bob", result[1].name)
    }

    @Test
    fun `resolves single player`() = runTest {
        cache.put("Alice", player("Alice", "id-alice"))

        val result = pubgApi.resolvePlayers("Alice", cache = cache)

        assertEquals(1, result.size)
        assertEquals("Alice", result[0].name)
    }
}

private fun player(name: String, id: String) = Player(
    id = id,
    banType = BanType.Innocent,
    clanId = null,
    name = name,
    titleId = "pubg",
    shardId = "steam",
    patchVersion = null,
    matches = emptyList(),
)
