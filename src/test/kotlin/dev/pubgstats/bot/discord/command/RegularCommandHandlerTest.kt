package dev.pubgstats.bot.discord.command

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class RegularCommandHandlerTest {

    @Test
    fun `parseParams runs before execute`() = runTest {
        val ctx = FakeCommandContext(strings = mapOf("name" to "shroud"))
        val handler = ParamsHandler()

        handler.handle(ctx)

        assertEquals("shroud", ctx.responses.single())
    }

    @Test
    fun `parseParams failure prevents execute from running`() = runTest {
        val ctx = FakeCommandContext()
        val handler = ParamsHandler()

        assertFailsWith<IllegalStateException> { handler.handle(ctx) }
        assertFalse(handler.executeWasCalled)
    }

    @Test
    fun `optional params are nullable`() = runTest {
        val ctx = FakeCommandContext(strings = mapOf("name" to "shroud"))
        val handler = ParamsHandler()

        handler.handle(ctx)

        assertEquals("shroud", handler.lastParams?.name)
        assertEquals(null, handler.lastParams?.tag)
    }
}

private class ParamsHandler : RegularCommandHandler<ParamsHandler.Params>() {
    data class Params(val name: String, val tag: String?)

    override val name = "test"
    override val description = Localized(enUs = "Test handler", ptBr = "Handler de teste")

    var executeWasCalled = false
    var lastParams: Params? = null

    override fun parseParams(ctx: CommandContext) = Params(
        name = ctx.requireString("name"),
        tag = ctx.strings["tag"],
    )

    override suspend fun execute(ctx: CommandContext, params: Params) {
        executeWasCalled = true
        lastParams = params
        ctx.respond(params.name)
    }
}
