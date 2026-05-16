package dev.pubgstats.bot.cache

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes

class BoundedTtlCacheTest {
    private val cache = cache<String, String> {
        maximumSize = 10
        defaultTtl = 5.minutes
    }

    @Test
    fun `get returns null for missing key`() = runTest {
        assertNull(cache.get("unknown"))
    }

    @Test
    fun `put and get returns stored value`() = runTest {
        cache.put("key", "value")
        assertEquals("value", cache.get("key"))
    }

    @Test
    fun `put overwrites existing value`() = runTest {
        cache.put("key", "first")
        cache.put("key", "second")
        assertEquals("second", cache.get("key"))
    }

    @Test
    fun `invalidate removes entry`() = runTest {
        cache.put("key", "value")
        cache.invalidate("key")
        assertNull(cache.get("key"))
    }

    @Test
    fun `invalidateAll clears all entries`() = runTest {
        with(cache) {
            put("a", "1")
            put("b", "2")
            invalidateAll()
        }
        assertEquals(0, cache.size())
    }

    @Test
    fun `size reflects stored entries`() = runTest {
        assertEquals(0, cache.size())
        cache.put("a", "1")
        cache.put("b", "2")
        assertEquals(2, cache.size())
    }
}
