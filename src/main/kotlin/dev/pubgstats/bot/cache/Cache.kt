package dev.pubgstats.bot.cache

import kotlin.time.Duration

/**
 * A typed, suspend-friendly, thread-safe cache with TTL-based expiration.
 *
 * @param K the key type, must implement [equals] and [hashCode] correctly.
 * @param V the value type.
 */
interface Cache<K : Any, V : Any> {
    /** Returns the value for [key], or `null` if absent or expired. */
    suspend fun get(key: K): V?

    /**
     * Returns the value for [key] if cached, otherwise invokes [loader] and caches the result.
     * Concurrent callers for the same key share a single loader invocation.
     */
    suspend fun get(key: K, loader: suspend (K) -> V): V

    /** Stores [value] under [key] using the default TTL. */
    suspend fun put(key: K, value: V)

    /** Stores [value] under [key] with a custom [ttl] that overrides the default. */
    suspend fun put(key: K, value: V, ttl: Duration)

    /** Removes the entry for [key], if present. */
    suspend fun invalidate(key: K)

    /** Removes all entries from the cache. */
    suspend fun invalidateAll()

    /** Returns the number of entries currently stored (including not-yet-expired ones). */
    suspend fun size(): Int

    /** Snapshot of the cache's hit/miss/eviction statistics. Recomputed on each access. */
    val stats: CacheStats
}
