package dev.pubgstats.bot.discord.command

import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizedTest {

    @Test
    fun `resolves English value`() {
        val message = Localized(enUs = "Hello", ptBr = "Olá")
        assertEquals("Hello", message.resolve(BotLocale.EN_US))
    }

    @Test
    fun `resolves Portuguese value`() {
        val message = Localized(enUs = "Hello", ptBr = "Olá")
        assertEquals("Olá", message.resolve(BotLocale.PT_BR))
    }

    @Test
    fun `resolves non-string types`() {
        val embed = Localized(
            enUs = Embed(title = "Stats"),
            ptBr = Embed(title = "Estatísticas"),
        )
        assertEquals("Stats", embed.resolve(BotLocale.EN_US).title)
        assertEquals("Estatísticas", embed.resolve(BotLocale.PT_BR).title)
    }

    @Test
    fun `resolves parameterized messages`() {
        val message = Localized(
            enUs = { name: String -> "$name has joined" },
            ptBr = { name: String -> "$name entrou" },
        )
        assertEquals("shroud has joined", message.resolve(BotLocale.EN_US)("shroud"))
        assertEquals("shroud entrou", message.resolve(BotLocale.PT_BR)("shroud"))
    }
}
