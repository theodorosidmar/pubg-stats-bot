package dev.pubgstats.bot.cache

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.plusAssign
import kotlin.time.Clock
import kotlin.time.Duration

/**
 * Thread-safe, bounded cache with LRU eviction and TTL-based expiration.
 *
 * Uses [ConcurrentHashMap] for lock-free reads and [Mutex] for LRU tracking,
 * suspending cooperatively instead of blocking the carrier thread.
 */
@Suppress("TooManyFunctions")
internal class BoundedTtlCache<K : Any, V : Any>(
    private val maximumSize: Int,
    private val defaultTtl: Duration,
    private val clock: Clock,
) : Cache<K, V> {

    private val mutex = Mutex()

    /** Primary key-value store. Lock-free reads via [ConcurrentHashMap]. */
    private val store = ConcurrentHashMap<K, CacheEntry<V>>()

    /** Access-ordered map that tracks which keys were used most recently (LRU tail = eviction candidate). */
    private val accessOrder = LinkedHashMap<K, Unit>(
        /* initialCapacity = */
        maximumSize,
        /* loadFactor = */
        0.75f,
        /* accessOrder = */
        true,
    )

    /**
     * Loaders currently executing, keyed by the requested cache key.
     * Deduplicates concurrent loads for the same key.
     */
    private val pendingLoads = ConcurrentHashMap<K, Deferred<V>>()

    private val hits = AtomicLong(0)
    private val misses = AtomicLong(0)
    private val evictions = AtomicLong(0)

    override suspend fun get(key: K): V? {
        var entry = store[key] ?: return miss()
        val now = clock.now()
        return if (now >= entry.expiresAt) {
            remove(key)
            miss()
        } else {
            entry = entry.copy(lastAccessedAt = now)
            touchAccessOrder(key)
            hits += 1
            entry.value
        }
    }

    override suspend fun get(key: K, loader: suspend (K) -> V): V {
        get(key)?.let { return it }

        val deferred = CompletableDeferred<V>()
        val existing = pendingLoads.putIfAbsent(key, deferred)
        return existing?.await() ?: loadAndCache(key, deferred, loader)
    }

    override suspend fun put(key: K, value: V) {
        put(key, value, defaultTtl)
    }

    override suspend fun put(key: K, value: V, ttl: Duration) {
        val now = clock.now()
        evictIfNeeded(key)
        store[key] = CacheEntry(value = value, expiresAt = now + ttl, lastAccessedAt = now)
        touchAccessOrder(key)
    }

    override suspend fun invalidate(key: K) {
        remove(key)
    }

    override suspend fun invalidateAll() {
        store.clear()
        mutex.withLock { accessOrder.clear() }
    }

    override suspend fun size(): Int = store.size

    override val stats: CacheStats
        get() = CacheStats(
            hitCount = hits.load(),
            missCount = misses.load(),
            evictionCount = evictions.load(),
            size = store.size,
        )

    /** Invokes [loader], caches the result, and signals all coroutines awaiting this key. */
    @Suppress("TooGenericExceptionCaught")
    private suspend fun loadAndCache(key: K, deferred: CompletableDeferred<V>, loader: suspend (K) -> V): V = try {
        val value = loader(key)
        put(key, value)
        deferred.complete(value)
        value
    } catch (e: Exception) {
        deferred.completeExceptionally(e)
        throw e
    } finally {
        pendingLoads.remove(key, deferred)
    }

    private fun miss(): Nothing? {
        misses += 1
        return null
    }

    /** Evicts the LRU entry if the cache is at capacity and [incomingKey] is not already stored. */
    private suspend fun evictIfNeeded(incomingKey: K) {
        if (store.containsKey(incomingKey) || store.size < maximumSize) return
        val victim = mutex.withLock {
            accessOrder.keys.firstOrNull()
        } ?: return
        remove(victim)
        evictions += 1
    }

    private suspend fun remove(key: K) {
        store.remove(key)
        mutex.withLock { accessOrder.remove(key) }
    }

    private suspend fun touchAccessOrder(key: K) {
        mutex.withLock { accessOrder[key] = Unit }
    }
}
