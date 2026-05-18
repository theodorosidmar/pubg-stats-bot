package dev.pubgstats.bot.discord.command.duel.weapon

import dev.pubgkt.PubgApi
import dev.pubgkt.mastery.WeaponMastery
import dev.pubgkt.players.BanType
import dev.pubgkt.players.Player
import dev.pubgstats.bot.cache.Cache
import dev.pubgstats.bot.cache.cache
import dev.pubgstats.bot.discord.command.BotLocale
import dev.pubgstats.bot.discord.command.Caches
import dev.pubgstats.bot.discord.command.FakeCommandContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DuelWeaponCommandHandlerTest {

    private val weaponMasteryCache: Cache<String, WeaponMastery> = cache { maximumSize = 10 }
    private val handler = DuelWeaponCommandHandler(
        pubgApi = PubgApi(apiKey = "unused"),
        weaponMasteryCache = weaponMasteryCache,
    )

    private fun ctx(playerOne: String, playerTwo: String, weapon: Weapon) = FakeCommandContext(
        strings = mapOf(
            "player-one" to playerOne,
            "player-two" to playerTwo,
            "weapon" to weapon.id,
        ),
    )

    private suspend fun seedPlayers(vararg players: Player) {
        players.forEach { Caches.players.put(it.name, it) }
    }

    private suspend fun seedMastery(accountId: String, mastery: WeaponMastery) {
        weaponMasteryCache.put(accountId, mastery)
    }

    @Test
    fun `responds with embed when both players have weapon stats`() = runTest {
        seedPlayers(player("Alice", "id-alice"), player("Bob", "id-bob"))
        seedMastery("id-alice", mastery(aug = weaponSummary(kills = 50)))
        seedMastery("id-bob", mastery(aug = weaponSummary(kills = 30)))

        val ctx = ctx("Alice", "Bob", Weapon.AUG)
        handler.handle(ctx)

        assertEquals(1, ctx.embeds.size)
        assertTrue(ctx.responses.isEmpty())
    }

    @Test
    fun `responds with weapon not found when neither player has stats`() = runTest {
        seedPlayers(player("Alice", "id-alice"), player("Bob", "id-bob"))
        seedMastery("id-alice", mastery())
        seedMastery("id-bob", mastery())

        val ctx = ctx("Alice", "Bob", Weapon.AUG)
        handler.handle(ctx)

        assertEquals(1, ctx.responses.size)
        assertTrue(ctx.responses.first().contains("AUG"))
        assertTrue(ctx.embeds.isEmpty())
    }

    @Test
    fun `responds with no stats when player one has no stats for weapon`() = runTest {
        seedPlayers(player("Alice", "id-alice"), player("Bob", "id-bob"))
        seedMastery("id-alice", mastery())
        seedMastery("id-bob", mastery(aug = weaponSummary(kills = 10)))

        val ctx = ctx("Alice", "Bob", Weapon.AUG)
        handler.handle(ctx)

        assertEquals(1, ctx.responses.size)
        assertTrue(ctx.responses.first().contains("Alice"))
    }

    @Test
    fun `responds with no stats when player two has no stats for weapon`() = runTest {
        seedPlayers(player("Alice", "id-alice"), player("Bob", "id-bob"))
        seedMastery("id-alice", mastery(aug = weaponSummary(kills = 10)))
        seedMastery("id-bob", mastery())

        val ctx = ctx("Alice", "Bob", Weapon.AUG)
        handler.handle(ctx)

        assertEquals(1, ctx.responses.size)
        assertTrue(ctx.responses.first().contains("Bob"))
    }

    @Test
    fun `embed title contains both player names`() = runTest {
        seedPlayers(player("Alice", "id-alice"), player("Bob", "id-bob"))
        seedMastery("id-alice", mastery(aug = weaponSummary(kills = 10)))
        seedMastery("id-bob", mastery(aug = weaponSummary(kills = 5)))

        val ctx = ctx("Alice", "Bob", Weapon.AUG)
        handler.handle(ctx)

        val embed = ctx.embeds.first().resolve(BotLocale.EN_US)
        assertTrue(embed.title!!.contains("Alice"))
        assertTrue(embed.title!!.contains("Bob"))
    }

    @Test
    fun `fetches weapon mastery concurrently for both players`() = runTest {
        seedPlayers(player("Alice", "id-alice"), player("Bob", "id-bob"))
        seedMastery("id-alice", mastery(m416 = weaponSummary(kills = 100)))
        seedMastery("id-bob", mastery(m416 = weaponSummary(kills = 80)))

        val ctx = ctx("Alice", "Bob", Weapon.M416)
        handler.handle(ctx)

        assertEquals(1, ctx.embeds.size)
    }

    @Test
    fun `uses cached players on repeated calls`() = runTest {
        seedPlayers(player("Alice", "id-alice"), player("Bob", "id-bob"))
        seedMastery("id-alice", mastery(aug = weaponSummary(kills = 10)))
        seedMastery("id-bob", mastery(aug = weaponSummary(kills = 5)))

        val ctx1 = ctx("Alice", "Bob", Weapon.AUG)
        val ctx2 = ctx("Alice", "Bob", Weapon.AUG)
        handler.handle(ctx1)
        handler.handle(ctx2)

        assertEquals(1, ctx1.embeds.size)
        assertEquals(1, ctx2.embeds.size)
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
