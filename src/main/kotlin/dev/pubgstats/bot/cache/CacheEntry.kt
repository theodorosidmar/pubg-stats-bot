package dev.pubgstats.bot.cache

import kotlin.time.Instant

/** Wraps a cached value alongside its expiration and last-access metadata. */
internal data class CacheEntry<V>(
    val value: V,
    val expiresAt: Instant,
    val lastAccessedAt: Instant,
)
