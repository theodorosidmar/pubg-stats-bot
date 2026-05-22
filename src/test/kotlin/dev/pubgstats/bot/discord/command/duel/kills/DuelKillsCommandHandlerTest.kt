package dev.pubgstats.bot.discord.command.duel.kills

import dev.pubgkt.GameMode
import dev.pubgkt.PubgApi
import dev.pubgstats.bot.discord.command.CommandOption
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DuelKillsCommandHandlerTest {

    private val handler = DuelKillsCommandHandler(
        pubgApi = PubgApi(apiKey = "unused"),
    )

    @Test
    fun `has correct command name`() {
        assertEquals("kills", handler.name)
    }

    @Test
    fun `has three required options`() {
        assertEquals(3, handler.options.size)
        assertTrue(handler.options.all { (it as CommandOption.StringOption).required })
    }

    @Test
    fun `game-mode option has all game modes as choices`() {
        val gameModeOption = handler.options.first { it.name == "game-mode" } as CommandOption.StringOption
        assertEquals(GameMode.entries.size, gameModeOption.choices.size)
    }

    @Test
    fun `game-mode choices use lowercase hyphenated labels`() {
        val gameModeOption = handler.options.first { it.name == "game-mode" } as CommandOption.StringOption
        val squadFpp = gameModeOption.choices.first { it.value == "SQUAD_FPP" }
        assertEquals("squad-fpp", squadFpp.label)
    }
}
