package dev.pubgstats.bot.cache

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class CacheStatsTest {
    private val clock = FixedClock()
    private val cache = cache<String, String> {
        maximumSize = 2
        defaultTtl = 5.minutes
        clock = this@CacheStatsTest.clock
    }

    @Test
    fun `records hits and misses`() = runTest {
        with(cache) {
            put("key", "value")
            get("key")
            get("missing")
        }

        val stats = cache.stats
        assertEquals(1L, stats.hitCount)
        assertEquals(1L, stats.missCount)
    }

    @Test
    fun `records evictions`() = runTest {
        with(cache) {
            put("a", "1")
            put("b", "2")
            put("c", "3")
        }

        assertEquals(1L, cache.stats.evictionCount)
    }

    @Test
    fun `expired entry counts as miss`() = runTest {
        cache.put("key", "value")
        clock.advance(5.minutes)
        cache.get("key")

        assertEquals(0L, cache.stats.hitCount)
        assertEquals(1L, cache.stats.missCount)
    }

    @Test
    fun `hit rate is calculated correctly`() = runTest {
        with(cache) {
            put("key", "value")
            get("key")
            get("missing")
        }

        assertEquals(0.5, cache.stats.hitRate, 0.01)
    }

    @Test
    fun `hit rate is zero when no requests`() {
        assertEquals(0.0, cache.stats.hitRate)
    }

    @Test
    fun `size reflects current entries`() = runTest {
        cache.put("a", "1")
        assertEquals(1, cache.stats.size)
    }
}
