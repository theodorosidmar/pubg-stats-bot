package dev.pubgstats.bot.discord.command

import dev.pubgkt.mastery.WeaponMastery
import dev.pubgkt.players.Player
import dev.pubgstats.bot.cache.Cache
import dev.pubgstats.bot.cache.cache
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object Caches {
    val players: Cache<String, Player> = cache {
        maximumSize = 200
        defaultTtl = 1.hours
    }

    val weaponMastery: Cache<String, WeaponMastery> = cache {
        maximumSize = 20
        defaultTtl = 10.minutes
    }
}
