package dev.pubgstats.bot.discord.command

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PingHandlerTest {

    @Test
    fun `responds with pong`() = runTest {
        val ctx = FakeCommandContext()
        PingHandler().handle(ctx)
        assertEquals("pong", ctx.responses.single())
    }
}
