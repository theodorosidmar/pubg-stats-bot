package dev.pubgstats.bot.cache

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * DSL for constructing [Cache] instances.
 *
 * Usage:
 * ```
 * val playerCache = cache<String, Player> {
 *     maximumSize = 500
 *     defaultTtl = 5.minutes
 * }
 * ```
 */
class CacheBuilder<K : Any, V : Any> internal constructor() {
    /** Maximum number of entries the cache can hold before eviction. */
    var maximumSize: Int = 100
        set(value) {
            require(value > 0) { "maximumSize must be positive, got $value" }
            field = value
        }

    /** Default time-to-live for entries. */
    var defaultTtl: Duration = 5.minutes
        set(value) {
            require(value.isPositive()) { "defaultTtl must be positive, got $value" }
            field = value
        }

    /** Eviction policy used when the cache reaches capacity. */
    var evictionPolicy: EvictionPolicy = EvictionPolicy.LRU

    /** Clock used for TTL calculations. Useful for injecting a fixed clock in tests. */
    var clock: Clock = Clock.System

    internal fun build(): Cache<K, V> = BoundedTtlCache(
        maximumSize = maximumSize,
        defaultTtl = defaultTtl,
        clock = clock,
    )
}

/** Creates a new [Cache] configured by the given [block]. */
fun <K : Any, V : Any> cache(block: CacheBuilder<K, V>.() -> Unit): Cache<K, V> =
    CacheBuilder<K, V>().apply(block).build()
