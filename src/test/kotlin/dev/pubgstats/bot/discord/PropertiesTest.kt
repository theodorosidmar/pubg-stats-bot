package dev.pubgstats.bot.discord

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

class PropertiesTest {

    @Test
    fun `user input property - args take highest precedence`() {
        System.setProperty(DiscordTokenProperty.systemPropertyKey, "from-sysprop")
        try {
            val result = resolveProperty(
                args = arrayOf("${DiscordTokenProperty.argPrefix}from-arg"),
                property = DiscordTokenProperty,
            ) {
                fail("onNull should not be called")
            }
            assertEquals("from-arg", result)
        } finally {
            System.clearProperty(DiscordTokenProperty.systemPropertyKey)
        }
    }

    @Test
    fun `user input property - system property takes precedence over env var`() {
        System.setProperty(DiscordTokenProperty.systemPropertyKey, "from-sysprop")
        try {
            val result = resolveProperty(
                args = emptyArray(),
                property = DiscordTokenProperty,
            ) {
                fail("onNull should not be called")
            }
            assertEquals("from-sysprop", result)
        } finally {
            System.clearProperty(DiscordTokenProperty.systemPropertyKey)
        }
    }

    @Test
    fun `user input property - onNull is called when all sources absent`() {
        var called = false
        assertFailsWith<TestHaltException> {
            resolveProperty(
                args = emptyArray(),
                property = DiscordTokenProperty,
            ) {
                called = true
                throw TestHaltException()
            }
        }
        assertTrue(called)
    }

    @Test
    fun `user input property - arg prefix must match exactly`() {
        var called = false
        assertFailsWith<TestHaltException> {
            resolveProperty(
                args = arrayOf("--discord-tokenx=wrong", "--other=val"),
                property = DiscordTokenProperty,
            ) {
                called = true
                throw TestHaltException()
            }
        }
        assertTrue(called)
    }

    @Test
    fun `user input property - arg value can contain equals sign`() {
        val result = resolveProperty(
            args = arrayOf("${DiscordTokenProperty.argPrefix}abc=def"),
            property = DiscordTokenProperty,
        ) {
            fail("onNull should not be called")
        }
        assertEquals("abc=def", result)
    }

    @Test
    fun `defaulted property - default is used when all sources absent`() {
        val result = resolveProperty(
            args = emptyArray(),
            property = WorkerProperty,
        )
        assertEquals(Runtime.getRuntime().availableProcessors(), result)
    }

    @Test
    fun `defaulted property - parses value from args`() {
        val result = resolveProperty(
            args = arrayOf("${WorkerProperty.argPrefix}8"),
            property = WorkerProperty,
        )
        assertEquals(8, result)
    }
}

private class TestHaltException : RuntimeException()
