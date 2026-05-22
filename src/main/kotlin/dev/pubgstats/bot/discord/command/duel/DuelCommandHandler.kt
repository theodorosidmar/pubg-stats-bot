package dev.pubgstats.bot.discord.command.duel

import dev.pubgkt.PubgApi
import dev.pubgstats.bot.discord.command.Caches
import dev.pubgstats.bot.discord.command.Localized
import dev.pubgstats.bot.discord.command.ParentCommandHandler
import dev.pubgstats.bot.discord.command.RegularCommandHandler
import dev.pubgstats.bot.discord.command.duel.kills.DuelKillsCommandHandler
import dev.pubgstats.bot.discord.command.duel.weapon.DuelWeaponCommandHandler

class DuelCommandHandler(pubgApi: PubgApi) : ParentCommandHandler() {
    override val name: String = "duel"

    override val description: Localized<String> = Localized(
        enUs = "Duel with your friends or other players to see who is the best",
        ptBr = "Compare suas estatísticas com as de outro jogador para ver quem é o melhor",
    )

    override val childHandlers: List<RegularCommandHandler<*>> = listOf(
        DuelWeaponCommandHandler(
            pubgApi = pubgApi,
            weaponMasteryCache = Caches.weaponMastery,
        ),
        DuelKillsCommandHandler(
            pubgApi = pubgApi,
        ),
    )
}
