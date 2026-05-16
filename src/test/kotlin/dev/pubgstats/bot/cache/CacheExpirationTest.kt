package dev.pubgstats.bot.cache

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CacheExpirationTest {
    private val clock = FixedClock()
    private val cache = cache<String, String> {
        maximumSize = 10
        defaultTtl = 5.minutes
        clock = this@CacheExpirationTest.clock
    }

    @Test
    fun `entry is available before TTL expires`() = runTest {
        cache.put("key", "value")
        clock.advance(4.minutes)
        assertEquals("value", cache.get("key"))
    }

    @Test
    fun `entry expires after TTL`() = runTest {
        cache.put("key", "value")
        clock.advance(5.minutes)
        assertNull(cache.get("key"))
    }

    @Test
    fun `per-entry TTL overrides default`() = runTest {
        cache.put("short", "value", ttl = 30.seconds)
        cache.put("long", "value", ttl = 10.minutes)

        clock.advance(1.minutes)

        assertNull(cache.get("short"))
        assertEquals("value", cache.get("long"))
    }

    @Test
    fun `expired entry is removed from size count`() = runTest {
        cache.put("key", "value")
        clock.advance(5.minutes)
        cache.get("key")
        assertEquals(0, cache.size())
    }
}
