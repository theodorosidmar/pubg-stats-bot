package dev.pubgstats.bot.discord.command

/**
 * A value translated for all supported locales. The compiler enforces completeness:
 * every constructor parameter is required, and [resolve] uses an exhaustive `when`.
 */
data class Localized<T>(val enUs: T, val ptBr: T) {
    fun resolve(locale: BotLocale): T = when (locale) {
        BotLocale.EN_US -> enUs
        BotLocale.PT_BR -> ptBr
    }
}
