package dev.pubgstats.bot.discord.command.duel

import dev.pubgstats.bot.discord.command.BotLocale
import dev.pubgstats.bot.discord.command.Localized
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DuelEmbedBuilderTest {

    private enum class TestStat(
        override val label: Localized<String>,
        override val isDecimal: Boolean = false,
        override val comparator: Comparator<Double> = naturalOrder(),
    ) : DuelStat {
        INTEGER_STAT(Localized(enUs = "Integer Stat", ptBr = "Stat Inteiro")),
        DECIMAL_STAT(Localized(enUs = "Decimal Stat", ptBr = "Stat Decimal"), isDecimal = true),
        LOWER_IS_BETTER(
            Localized(enUs = "Lower Wins", ptBr = "Menor Vence"),
            comparator = reverseOrder(),
        ),
    }

    @Test
    fun `custom comparator reverses winner detection`() {
        val result = buildDuelEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            description = Localized(enUs = "Test", ptBr = "Teste"),
            stats = listOf(TestStat.LOWER_IS_BETTER),
            statValue = { 10.0 to 5.0 },
        )
        val field = result.enUs.fields.first { it.name == "Lower Wins" }

        assertTrue(field.value.contains("**Bob**"))
    }

    @Test
    fun `all stats tied results in yellow color and tie text`() {
        val result = buildDuelEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            description = Localized(enUs = "Test", ptBr = "Teste"),
            stats = TestStat.entries,
            statValue = { 10.0 to 10.0 },
        )

        assertEquals(0xFEE75C, result.enUs.color)
        assertTrue(result.enUs.fields.last().value.contains("tie") || result.enUs.fields.last().value.contains("Tie"))
    }

    @Test
    fun `isDecimal true formats with two decimal places`() {
        val result = buildDuelEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            description = Localized(enUs = "Test", ptBr = "Teste"),
            stats = listOf(TestStat.DECIMAL_STAT),
            statValue = { 1234.5 to 999.0 },
        )
        val field = result.enUs.fields.first { it.name == "Decimal Stat" }

        assertTrue(field.value.contains("1,234.50"))
        assertTrue(field.value.contains("999.00"))
    }

    @Test
    fun `isDecimal false formats as integer`() {
        val result = buildDuelEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            description = Localized(enUs = "Test", ptBr = "Teste"),
            stats = listOf(TestStat.INTEGER_STAT),
            statValue = { 1234.0 to 999.0 },
        )
        val field = result.enUs.fields.first { it.name == "Integer Stat" }

        assertTrue(field.value.contains("1,234"))
        assertTrue("1,234." !in field.value)
    }

    @Test
    fun `en-us uses comma as thousands separator`() {
        val result = buildDuelEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            description = Localized(enUs = "Test", ptBr = "Teste"),
            stats = listOf(TestStat.INTEGER_STAT),
            statValue = { 10000.0 to 5000.0 },
        )
        val field = result.enUs.fields.first { it.name == "Integer Stat" }

        assertTrue(field.value.contains("10,000"))
        assertTrue(field.value.contains("5,000"))
    }

    @Test
    fun `pt-br uses dot as thousands separator and comma for decimals`() {
        val result = buildDuelEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            description = Localized(enUs = "Test", ptBr = "Teste"),
            stats = listOf(TestStat.DECIMAL_STAT),
            statValue = { 1234.5 to 999.0 },
        )
        val field = result.ptBr.fields.first {
            it.name == TestStat.DECIMAL_STAT.label.resolve(BotLocale.PT_BR)
        }

        assertTrue(field.value.contains("1.234,50"))
        assertTrue(field.value.contains("999,00"))
    }

    @Test
    fun `score field reflects comparator-based scoring`() {
        val result = buildDuelEmbed(
            playerOneName = "Alice",
            playerTwoName = "Bob",
            description = Localized(enUs = "Test", ptBr = "Teste"),
            stats = listOf(TestStat.LOWER_IS_BETTER, TestStat.INTEGER_STAT),
            statValue = { stat ->
                when (stat) {
                    TestStat.LOWER_IS_BETTER -> 10.0 to 5.0
                    else -> 100.0 to 50.0
                }
            },
        )
        val scoreField = result.enUs.fields.last()

        assertTrue(scoreField.value.contains("1 x 1"))
    }
}
