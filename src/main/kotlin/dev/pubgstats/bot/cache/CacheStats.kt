package dev.pubgstats.bot.cache

/** Snapshot of a cache's hit/miss/eviction counters. */
data class CacheStats(
    val hitCount: Long,
    val missCount: Long,
    val evictionCount: Long,
    val size: Int,
) {
    /** Total number of get operations (hits + misses). */
    val requestCount: Long = hitCount + missCount

    /** Ratio of hits to total requests, between 0.0 and 1.0. Returns 0.0 if no requests. */
    val hitRate: Double = if (requestCount == 0L) {
        0.0
    } else {
        hitCount.toDouble() / requestCount
    }
}
