package dev.pubgstats.bot.cache

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.plusAssign
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class CacheLoaderTest {
    private val cache = cache<String, String> {
        maximumSize = 10
        defaultTtl = 5.minutes
    }

    @Test
    fun `loader is called on cache miss`() = runTest {
        val result = cache.get("key") { "loaded-$it" }
        assertEquals("loaded-key", result)
    }

    @Test
    fun `loader result is cached`() = runTest {
        val callCount = AtomicInt(0)
        cache.get("key") {
            callCount += 1
            "value"
        }
        cache.get("key") {
            callCount += 1
            "value"
        }
        assertEquals(1, callCount.load())
    }

    @Test
    fun `loader is not called on cache hit`() = runTest {
        cache.put("key", "existing")
        val result = cache.get("key") { "should-not-be-called" }
        assertEquals("existing", result)
    }

    @Test
    fun `concurrent loaders for same key are deduplicated`() = runTest {
        val callCount = AtomicInt(0)
        val results = (1..10).map {
            async {
                cache.get("key") { k ->
                    callCount += 1
                    delay(50.milliseconds)
                    "loaded-$k"
                }
            }
        }.awaitAll()

        assertEquals(1, callCount.load())
        results.forEach { assertEquals("loaded-key", it) }
    }

    @Test
    fun `loader exception propagates to caller`() = runTest {
        assertFailsWith<IllegalStateException> {
            cache.get("key") { throw IllegalStateException("api error") }
        }
    }

    @Test
    fun `failed loader allows retry`() = runTest {
        runCatching {
            cache.get("key") { throw IllegalStateException("first attempt") }
        }
        val result = cache.get("key") { "recovered" }
        assertEquals("recovered", result)
    }
}
