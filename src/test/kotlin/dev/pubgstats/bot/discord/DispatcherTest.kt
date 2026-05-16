package dev.pubgstats.bot.discord

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.concurrent.atomics.updateAndFetch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DispatcherTest {

    @Test
    @Suppress("SleepInsteadOfDelay")
    fun `gateway dispatcher - fixed thread pool bounds concurrency`() = runTest {
        val workers = 2
        val dispatcher = Executors.newFixedThreadPool(workers).asCoroutineDispatcher()
        val threadNames = ConcurrentHashMap.newKeySet<String>()

        val scope = CoroutineScope(SupervisorJob() + dispatcher)
        val jobs = List(50) {
            scope.async {
                val rawName = Thread.currentThread().name.substringBefore(" @")
                threadNames.add(rawName)
                Thread.sleep(10)
            }
        }
        jobs.awaitAll()

        assertTrue(
            threadNames.size <= workers,
            "Expected at most $workers threads, got ${threadNames.size}: $threadNames",
        )
        dispatcher.close()
    }

    @Test
    @OptIn(ExperimentalAtomicApi::class)
    @Suppress("SleepInsteadOfDelay")
    fun `processing dispatcher - virtual threads run tasks concurrently`() = runTest {
        val dispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
        val concurrentCount = AtomicInt(0)
        val peakConcurrency = AtomicInt(0)

        val scope = CoroutineScope(SupervisorJob() + dispatcher)
        val jobs = List(50) {
            scope.async {
                val current = concurrentCount.incrementAndFetch()
                peakConcurrency.updateAndFetch { peak -> maxOf(peak, current) }
                Thread.sleep(50)
                concurrentCount.decrementAndFetch()
            }
        }
        jobs.awaitAll()

        assertTrue(
            peakConcurrency.load() > 2,
            "Virtual threads should run more than 2 tasks concurrently, peak was ${peakConcurrency.load()}",
        )
        dispatcher.close()
    }

    @Test
    fun `processing scope - supervisor job does not cancel siblings on child failure`() = runTest {
        val dispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
        val scope = CoroutineScope(SupervisorJob() + dispatcher)

        val failingJob = scope.async {
            error("intentional failure")
        }
        val succeedingJob = scope.async {
            "success"
        }

        try {
            failingJob.await()
        } catch (expected: IllegalStateException) {
            assertEquals("intentional failure", expected.message)
        }

        assertEquals("success", succeedingJob.await())
        assertTrue(scope.coroutineContext.job.isActive)
        dispatcher.close()
    }
}
