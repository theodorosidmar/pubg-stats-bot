package dev.pubgstats.bot.discord.command

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CommandContextTest {

    @Test
    fun `requireString returns value when present`() {
        val ctx = FakeCommandContext(strings = mapOf("player" to "shroud"))
        assertEquals("shroud", ctx.requireString("player"))
    }

    @Test
    fun `requireString throws when option is missing`() {
        val ctx = FakeCommandContext()
        val error = assertFailsWith<IllegalStateException> { ctx.requireString("player") }
        assertEquals("Missing required string option: player", error.message)
    }

    @Test
    fun `requireInteger returns value when present`() {
        val ctx = FakeCommandContext(integers = mapOf("count" to 5L))
        assertEquals(5L, ctx.requireInteger("count"))
    }

    @Test
    fun `requireInteger throws when option is missing`() {
        val ctx = FakeCommandContext()
        val error = assertFailsWith<IllegalStateException> { ctx.requireInteger("count") }
        assertEquals("Missing required integer option: count", error.message)
    }

    @Test
    fun `requireBoolean returns value when present`() {
        val ctx = FakeCommandContext(booleans = mapOf("verbose" to true))
        assertEquals(true, ctx.requireBoolean("verbose"))
    }

    @Test
    fun `requireBoolean throws when option is missing`() {
        val ctx = FakeCommandContext()
        val error = assertFailsWith<IllegalStateException> { ctx.requireBoolean("verbose") }
        assertEquals("Missing required boolean option: verbose", error.message)
    }
}
