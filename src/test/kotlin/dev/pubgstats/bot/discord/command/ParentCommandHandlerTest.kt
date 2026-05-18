package dev.pubgstats.bot.discord.command

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParentCommandHandlerTest {

    @Test
    fun `dispatches to matching child handler`() = runTest {
        val parent = DuelHandler()
        val ctx = FakeCommandContext(subCommandName = "kills")

        parent.handle(ctx)

        assertEquals("kills duel", ctx.responses.single())
    }

    @Test
    fun `dispatches to different child based on subCommandName`() = runTest {
        val parent = DuelHandler()
        val ctx = FakeCommandContext(subCommandName = "wins")

        parent.handle(ctx)

        assertEquals("wins duel", ctx.responses.single())
    }

    @Test
    fun `throws when no child matches subCommandName`() = runTest {
        val parent = DuelHandler()
        val ctx = FakeCommandContext(subCommandName = "unknown")

        assertFailsWith<NoSuchElementException> { parent.handle(ctx) }
    }

    @Test
    fun `child handler receives full context`() = runTest {
        val parent = DuelHandler()
        val ctx = FakeCommandContext(
            subCommandName = "kills",
            strings = mapOf("player" to "shroud"),
            locale = BotLocale.PT_BR,
        )

        parent.handle(ctx)

        assertEquals("kills duel: shroud", ctx.responses.single())
    }
}

private class DuelHandler : ParentCommandHandler() {
    override val name = "duel"
    override val description = Localized(enUs = "Duel commands", ptBr = "Comandos de duelo")
    override val childHandlers = listOf(KillsSubCommand(), WinsSubCommand())
}

private class KillsSubCommand : RegularCommandHandler<Unit>() {
    override val name = "kills"
    override val description = Localized(enUs = "Kills duel", ptBr = "Duelo de kills")

    override fun parseParams(ctx: CommandContext) = Unit

    override suspend fun execute(ctx: CommandContext, params: Unit) {
        val player = ctx.strings["player"]
        ctx.respond(if (player != null) "kills duel: $player" else "kills duel")
    }
}

private class WinsSubCommand : RegularCommandHandler<Unit>() {
    override val name = "wins"
    override val description = Localized(enUs = "Wins duel", ptBr = "Duelo de wins")

    override fun parseParams(ctx: CommandContext) = Unit

    override suspend fun execute(ctx: CommandContext, params: Unit) {
        ctx.respond("wins duel")
    }
}
