package dev.pubgstats.bot.cache

/** Strategy used to select which entry to remove when the cache reaches capacity. */
enum class EvictionPolicy {
    /** Least Recently Used — evicts the entry that has not been accessed for the longest time. */
    LRU,
}
