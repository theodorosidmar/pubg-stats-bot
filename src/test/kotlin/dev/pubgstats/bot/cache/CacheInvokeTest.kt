package dev.pubgstats.bot.cache

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CacheInvokeTest {

    private val testCache: Cache<String, String> = cache {
        maximumSize = 10
    }

    @Test
    fun `joins key parts with colon`() = runTest {
        val result = testCache("player1", "player2") { key ->
            assertEquals("player1:player2", key)
            "value"
        }
        assertEquals("value", result)
    }

    @Test
    fun `returns cached value on second call`() = runTest {
        var loadCount = 0
        val loader: suspend (String) -> String = {
            loadCount++
            "loaded"
        }

        testCache("a", "b", loader = loader)
        testCache("a", "b", loader = loader)

        assertEquals(1, loadCount)
    }

    @Test
    fun `different key parts invoke loader separately`() = runTest {
        var loadCount = 0
        val loader: suspend (String) -> String = { key ->
            loadCount++
            "value-for-$key"
        }

        val first = testCache("a", "b", loader = loader)
        val second = testCache("c", "d", loader = loader)

        assertEquals(2, loadCount)
        assertEquals("value-for-a:b", first)
        assertEquals("value-for-c:d", second)
    }

    @Test
    fun `single key part works without separator`() = runTest {
        val result = testCache("only-one") { "single" }
        assertEquals("single", result)
    }
}
