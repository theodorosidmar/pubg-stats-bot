package dev.pubgstats.bot.discord.command.duel.weapon

import dev.pubgstats.bot.discord.command.BotLocale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DuelWeaponEmbedBuilderTest {

    @Test
    fun `player one wins overall`() {
        val p1 = weaponSummary(
            xpTotal = 100, currentLevel = 5, currentTier = 3,
            kills = 50, mostKillsInAGame = 10, headShots = 20,
            mostHeadShotsInAGame = 5, damagePlayer = 5000.0,
            mostDamagePlayerInAGame = 800.0, longestDefeat = 300.0,
        )
        val p2 = weaponSummary(
            xpTotal = 80, currentLevel = 4, currentTier = 2,
            kills = 30, mostKillsInAGame = 7, headShots = 10,
            mostHeadShotsInAGame = 3, damagePlayer = 3000.0,
            mostDamagePlayerInAGame = 600.0, longestDefeat = 200.0,
        )

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.AUG,
            playerOneStats = p1,
            playerTwoStats = p2,
        )
        val embed = result.enUs

        assertEquals(0x57F287, embed.color)
        assertTrue(embed.fields.last().value.contains("Alice"))
        assertTrue(embed.fields.last().value.contains("${DuelWeaponStat.entries.size} x 0"))
    }

    @Test
    fun `player two wins overall`() {
        val p1 = weaponSummary(kills = 10)
        val p2 = weaponSummary(kills = 50, headShots = 20, mostKillsInAGame = 8)

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.AKM,
            playerOneStats = p1,
            playerTwoStats = p2,
        )
        val embed = result.enUs

        assertEquals(0x57F287, embed.color)
        assertTrue(embed.fields.last().value.contains("Bob"))
    }

    @Test
    fun `tie results in yellow color`() {
        val stats = weaponSummary(
            xpTotal = 100, currentLevel = 5, currentTier = 3,
            kills = 50, mostKillsInAGame = 10, headShots = 20,
            mostHeadShotsInAGame = 5, damagePlayer = 5000.0,
            mostDamagePlayerInAGame = 800.0, longestDefeat = 300.0,
        )

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.M416,
            playerOneStats = stats,
            playerTwoStats = stats,
        )
        val embed = result.enUs

        assertEquals(0xFEE75C, embed.color)
        assertTrue(embed.fields.last().value.contains("tie") || embed.fields.last().value.contains("Tie"))
    }

    @Test
    fun `has correct number of fields`() {
        val p1 = weaponSummary(kills = 10)
        val p2 = weaponSummary(kills = 5)

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.AUG,
            playerOneStats = p1,
            playerTwoStats = p2,
        )

        assertEquals(DuelWeaponStat.entries.size + 1, result.enUs.fields.size)
    }

    @Test
    fun `formats double stats with two decimals`() {
        val p1 = weaponSummary(damagePlayer = 1234.5)
        val p2 = weaponSummary(damagePlayer = 999.9)

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.AUG,
            playerOneStats = p1,
            playerTwoStats = p2,
        )
        val damageField = result.enUs.fields.first { it.name == "Total Damage" }

        assertTrue(damageField.value.contains("1,234.50"))
        assertTrue(damageField.value.contains("999.90"))
    }

    @Test
    fun `formats integer stats without decimals`() {
        val p1 = weaponSummary(kills = 42)
        val p2 = weaponSummary(kills = 38)

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.AUG,
            playerOneStats = p1,
            playerTwoStats = p2,
        )
        val killsField = result.enUs.fields.first { it.name == "Kills" }

        assertTrue(killsField.value.contains("42"))
        assertTrue(killsField.value.contains("38"))
    }

    @Test
    fun `both locales have same number of fields`() {
        val p1 = weaponSummary(kills = 10)
        val p2 = weaponSummary(kills = 5)

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.AUG,
            playerOneStats = p1,
            playerTwoStats = p2,
        )

        assertEquals(result.enUs.fields.size, result.ptBr.fields.size)
    }

    @Test
    fun `pt-br locale uses translated labels`() {
        val p1 = weaponSummary(kills = 10)
        val p2 = weaponSummary(kills = 5)

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.AUG,
            playerOneStats = p1,
            playerTwoStats = p2,
        )
        val killsField = result.ptBr.fields.first {
            it.name == DuelWeaponMessages.statLabel(DuelWeaponStat.KILLS).resolve(BotLocale.PT_BR)
        }

        assertTrue(killsField.value.contains("10"))
    }

    @Test
    fun `winning field has green circle for winner and red for loser`() {
        val p1 = weaponSummary(kills = 50)
        val p2 = weaponSummary(kills = 10)

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.AUG,
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
        val stats = weaponSummary(kills = 50)

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.AUG,
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
        val p1 = weaponSummary()
        val p2 = weaponSummary()

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.AUG,
            playerOneStats = p1,
            playerTwoStats = p2,
        )

        assertTrue(result.enUs.title!!.contains("Alice"))
        assertTrue(result.enUs.title!!.contains("Bob"))
    }

    @Test
    fun `description contains weapon name uppercased`() {
        val p1 = weaponSummary()
        val p2 = weaponSummary()

        val result = buildDuelWeaponEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            weaponName = Weapon.AUG,
            playerOneStats = p1,
            playerTwoStats = p2,
        )

        assertTrue(result.enUs.description!!.contains("AUG"))
    }
}
