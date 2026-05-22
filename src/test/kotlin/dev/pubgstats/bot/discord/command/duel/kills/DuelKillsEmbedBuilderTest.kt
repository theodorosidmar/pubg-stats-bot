package dev.pubgstats.bot.discord.command.duel.kills

import dev.pubgkt.GameMode
import dev.pubgstats.bot.discord.command.BotLocale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DuelKillsEmbedBuilderTest {

    @Test
    fun `player one wins overall`() {
        val p1 = lifetimeGameModeStats(
            damageDealt = 5000.0,
            headshotKills = 20,
            kills = 50,
            longestKill = 300.0,
            roadKills = 5,
            roundMostKills = 10,
        )
        val p2 = lifetimeGameModeStats(
            damageDealt = 3000.0,
            headshotKills = 10,
            kills = 30,
            longestKill = 200.0,
            roadKills = 2,
            roundMostKills = 7,
        )

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = p1,
            playerTwoStats = p2,
        )
        val embed = result.enUs

        assertEquals(0x57F287, embed.color)
        assertTrue(embed.fields.last().value.contains("Alice"))
        assertTrue(embed.fields.last().value.contains("${DuelKillsStat.entries.size} x 0"))
    }

    @Test
    fun `player two wins overall`() {
        val p1 = lifetimeGameModeStats(kills = 10)
        val p2 = lifetimeGameModeStats(kills = 50, headshotKills = 20, roundMostKills = 8)

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = p1,
            playerTwoStats = p2,
        )
        val embed = result.enUs

        assertEquals(0x57F287, embed.color)
        assertTrue(embed.fields.last().value.contains("Bob"))
    }

    @Test
    fun `tie results in yellow color`() {
        val stats = lifetimeGameModeStats(
            damageDealt = 5000.0,
            headshotKills = 20,
            kills = 50,
            longestKill = 300.0,
            roadKills = 5,
            roundMostKills = 10,
        )

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = stats,
            playerTwoStats = stats,
        )
        val embed = result.enUs

        assertEquals(0xFEE75C, embed.color)
        assertTrue(embed.fields.last().value.contains("tie") || embed.fields.last().value.contains("Tie"))
    }

    @Test
    fun `has correct number of fields`() {
        val p1 = lifetimeGameModeStats(kills = 10)
        val p2 = lifetimeGameModeStats(kills = 5)

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = p1,
            playerTwoStats = p2,
        )

        assertEquals(DuelKillsStat.entries.size + 1, result.enUs.fields.size)
    }

    @Test
    fun `formats decimal stats with two decimals`() {
        val p1 = lifetimeGameModeStats(damageDealt = 1234.5)
        val p2 = lifetimeGameModeStats(damageDealt = 999.9)

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = p1,
            playerTwoStats = p2,
        )
        val damageField = result.enUs.fields.first { it.name == "Damage dealt" }

        assertTrue(damageField.value.contains("1,234.50"))
        assertTrue(damageField.value.contains("999.90"))
    }

    @Test
    fun `formats integer stats without decimals`() {
        val p1 = lifetimeGameModeStats(kills = 42)
        val p2 = lifetimeGameModeStats(kills = 38)

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = p1,
            playerTwoStats = p2,
        )
        val killsField = result.enUs.fields.first { it.name == "Kills" }

        assertTrue(killsField.value.contains("42"))
        assertTrue(killsField.value.contains("38"))
    }

    @Test
    fun `both locales have same number of fields`() {
        val p1 = lifetimeGameModeStats(kills = 10)
        val p2 = lifetimeGameModeStats(kills = 5)

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = p1,
            playerTwoStats = p2,
        )

        assertEquals(result.enUs.fields.size, result.ptBr.fields.size)
    }

    @Test
    fun `pt-br locale uses translated labels`() {
        val p1 = lifetimeGameModeStats(kills = 10)
        val p2 = lifetimeGameModeStats(kills = 5)

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = p1,
            playerTwoStats = p2,
        )
        val killsField = result.ptBr.fields.first {
            it.name == DuelKillsStat.KILLS.label.resolve(BotLocale.PT_BR)
        }

        assertTrue(killsField.value.contains("10"))
    }

    @Test
    fun `winning field has green circle for winner and red for loser`() {
        val p1 = lifetimeGameModeStats(kills = 50)
        val p2 = lifetimeGameModeStats(kills = 10)

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = p1,
            playerTwoStats = p2,
        )
        val killsField = result.enUs.fields.first { it.name == "Kills" }

        assertTrue(killsField.value.contains("🟢"))
        assertTrue(killsField.value.contains("🔴"))
        assertTrue(killsField.value.contains("**Alice**"))
    }

    @Test
    fun `tied stat field has white circles`() {
        val stats = lifetimeGameModeStats(kills = 50)

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = stats,
            playerTwoStats = stats,
        )
        val killsField = result.enUs.fields.first { it.name == "Kills" }

        assertTrue(killsField.value.contains("⚪"))
        assertTrue("🟢" !in killsField.value)
        assertTrue("🔴" !in killsField.value)
    }

    @Test
    fun `title contains both player names`() {
        val p1 = lifetimeGameModeStats()
        val p2 = lifetimeGameModeStats()

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = p1,
            playerTwoStats = p2,
        )

        assertTrue(result.enUs.title!!.contains("Alice"))
        assertTrue(result.enUs.title!!.contains("Bob"))
    }

    @Test
    fun `description contains game mode`() {
        val p1 = lifetimeGameModeStats(gameMode = GameMode.SQUAD_FPP)
        val p2 = lifetimeGameModeStats(gameMode = GameMode.SQUAD_FPP)

        val result = buildDuelKillsEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            playerOneStats = p1,
            playerTwoStats = p2,
        )

        assertTrue(result.enUs.description!!.contains("SQUAD FPP"))
    }
}
