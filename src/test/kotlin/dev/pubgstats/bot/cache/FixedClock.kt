package dev.pubgstats.bot.cache

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

class FixedClock(private var now: Instant = Instant.fromEpochSeconds(0)) : Clock {
    override fun now(): Instant = now

    fun advance(duration: Duration) {
        now += duration
    }
}
