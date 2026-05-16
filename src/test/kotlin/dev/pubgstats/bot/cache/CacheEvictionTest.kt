package dev.pubgstats.bot.cache

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes

class CacheEvictionTest {
    private val cache = cache<String, String> {
        maximumSize = 3
        defaultTtl = 5.minutes
    }

    @Test
    fun `evicts least recently used when at capacity`() = runTest {
        with(cache) {
            put("a", "1")
            put("b", "2")
            put("c", "3")
            put("d", "4")
        }

        assertNull(cache.get("a"))
        assertEquals("2", cache.get("b"))
        assertEquals("3", cache.get("c"))
        assertEquals("4", cache.get("d"))
    }

    @Test
    fun `accessing an entry prevents its eviction`() = runTest {
        with(cache) {
            put("a", "1")
            put("b", "2")
            put("c", "3")
            get("a")
            put("d", "4")
        }

        assertEquals("1", cache.get("a"))
        assertNull(cache.get("b"))
    }

    @Test
    fun `overwriting existing key does not trigger eviction`() = runTest {
        with(cache) {
            put("a", "1")
            put("b", "2")
            put("c", "3")
            put("a", "updated")
        }

        assertEquals("updated", cache.get("a"))
        assertEquals("2", cache.get("b"))
        assertEquals("3", cache.get("c"))
        assertEquals(3, cache.size())
    }
}
