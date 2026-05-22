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
import dev.pubgstats.bot.discord.command.duel.DuelParams
import dev.pubgstats.bot.discord.command.duel.DuelSubCommandHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DuelWeaponCommandHandler(
    pubgApi: PubgApi,
    private val weaponMasteryCache: Cache<String, WeaponMastery>,
) : DuelSubCommandHandler<DuelWeaponCommandHandler.Params>(pubgApi) {
    override val name: String = "gun"
    override val description: Localized<String> = Localized(
        enUs = "Compare weapon stats between two players",
        ptBr = "Compare estatísticas de arma entre dois jogadores",
    )

    data class Params(
        override val playerOne: String,
        override val playerTwo: String,
        val weapon: Weapon,
    ) : DuelParams

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
        val (playerOne, playerTwo) = resolveDuelPlayers(params)

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
            ctx.respond(
                Localized(
                    enUs = "Weapon \"${params.weapon.label}\" not found.",
                    ptBr = "Arma \"${params.weapon.label}\" não encontrada.",
                ),
            )
            return@coroutineScope
        }
        if (p1WeaponStats == null) {
            ctx.respond(
                Localized(
                    enUs = "**${playerOne.name}** has no stats for \"${params.weapon.label}\".",
                    ptBr = "**${playerOne.name}** não possui estatísticas para \"${params.weapon.label}\".",
                ),
            )
            return@coroutineScope
        }
        if (p2WeaponStats == null) {
            ctx.respond(
                Localized(
                    enUs = "**${playerTwo.name}** has no stats for \"${params.weapon.label}\".",
                    ptBr = "**${playerTwo.name}** não possui estatísticas para \"${params.weapon.label}\".",
                ),
            )
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
