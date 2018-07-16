package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.core.executor.testing.CountingTaskExecutorRule
import com.kaizendeveloper.bitcoinsandbox.util.DaggerTest
import org.junit.After
import org.junit.Rule
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class DbTest : DaggerTest() {

    @get:Rule
    private val countingTaskExecutorRule = CountingTaskExecutorRule()

    @Suppress("unused")
    @Inject
    protected lateinit var db: SandboxDatabase

    @After
    fun closeDb() {
        countingTaskExecutorRule.drainTasks(10, TimeUnit.SECONDS)
        db.close()
    }
}
