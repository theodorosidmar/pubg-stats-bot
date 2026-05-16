package dev.pubgstats.bot.discord.command

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PingHandlerTest {

    @Test
    fun `responds with pong in English`() = runTest {
        val ctx = FakeCommandContext(locale = BotLocale.EN_US)
        PingHandler().handle(ctx)
        assertEquals("pong", ctx.responses.single())
    }

    @Test
    fun `responds with pong in Portuguese`() = runTest {
        val ctx = FakeCommandContext(locale = BotLocale.PT_BR)
        PingHandler().handle(ctx)
        assertEquals("pong", ctx.responses.single())
    }
}
