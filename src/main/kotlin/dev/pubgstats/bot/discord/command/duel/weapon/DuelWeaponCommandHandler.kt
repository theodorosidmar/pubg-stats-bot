package dev.pubgstats.bot.discord.command.duel.weapon

import dev.pubgkt.PubgApi
import dev.pubgkt.mastery.WeaponMastery
import dev.pubgkt.mastery.getWeaponMasteryByAccountId
import dev.pubgstats.bot.cache.Cache
import dev.pubgstats.bot.cache.invoke
import dev.pubgstats.bot.discord.command.CommandContext
import dev.pubgstats.bot.discord.command.CommandOption
import dev.pubgstats.bot.discord.command.Localized
import dev.pubgstats.bot.discord.command.PLAYER_ONE_OPTION
import dev.pubgstats.bot.discord.command.PLAYER_TWO_OPTION
import dev.pubgstats.bot.discord.command.RegularCommandHandler
import dev.pubgstats.bot.discord.command.resolvePlayers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DuelWeaponCommandHandler(
    private val pubgApi: PubgApi,
    private val weaponMasteryCache: Cache<String, WeaponMastery>,
) : RegularCommandHandler<DuelWeaponCommandHandler.Params>() {
    override val name: String = "gun"
    override val description: Localized<String> = Localized(
        enUs = "Compare weapon stats between two players",
        ptBr = "Compare estatísticas de arma entre dois jogadores",
    )

    data class Params(
        val playerOne: String,
        val playerTwo: String,
        val weapon: Weapon,
    )

    override val options: List<CommandOption> = listOf(
        PLAYER_ONE_OPTION,
        PLAYER_TWO_OPTION,
        CommandOption.StringOption(
            name = "weapon",
            description = Localized(
                enUs = "Weapon to compare",
                ptBr = "Arma para comparar",
            ),
            required = true,
            choices = Weapon.entries.map { it.toChoice() },
        ),
    )

    override fun parseParams(ctx: CommandContext): Params = Params(
        playerOne = ctx.requirePlayerOne(),
        playerTwo = ctx.requirePlayerTwo(),
        weapon = Weapon.forId(ctx.requireString("weapon")),
    )

    override suspend fun execute(ctx: CommandContext, params: Params): Unit = coroutineScope {
        val (playerOne, playerTwo) = pubgApi.resolvePlayers(params.playerOne, params.playerTwo)

        val p1MasteryDeferred = async {
            weaponMasteryCache(playerOne.id) { pubgApi.getWeaponMasteryByAccountId(playerOne.id) }
        }
        val p2MasteryDeferred = async {
            weaponMasteryCache(playerTwo.id) { pubgApi.getWeaponMasteryByAccountId(playerTwo.id) }
        }

        val p1Mastery = p1MasteryDeferred.await()
        val p2Mastery = p2MasteryDeferred.await()

        val p1WeaponStats = params.weapon.lookup(p1Mastery)
        val p2WeaponStats = params.weapon.lookup(p2Mastery)

        if (p1WeaponStats == null && p2WeaponStats == null) {
            ctx.respond(DuelWeaponMessages.weaponNotFound(params.weapon))
            return@coroutineScope
        }
        if (p1WeaponStats == null) {
            ctx.respond(DuelWeaponMessages.noStats(playerOne.name, params.weapon))
            return@coroutineScope
        }
        if (p2WeaponStats == null) {
            ctx.respond(DuelWeaponMessages.noStats(playerTwo.name, params.weapon))
            return@coroutineScope
        }

        ctx.respondEmbed(
            buildDuelWeaponEmbed(
                playerOneName = playerOne.name,
                playerTwoName = playerTwo.name,
                weaponName = params.weapon,
                playerOneStats = p1WeaponStats,
                playerTwoStats = p2WeaponStats,
            ),
        )
    }
}
