package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.core.executor.testing.CountingTaskExecutorRule
import com.kaizendeveloper.bitcoinsandbox.di.DaggerTest
import org.junit.After
import org.junit.Rule
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class DbTest : DaggerTest() {

    @get:Rule
    private val countingTaskExecutorRule = CountingTaskExecutorRule()

    @Inject
    protected lateinit var db: SandboxDatabase

    @After
    fun closeDb() {
        countingTaskExecutorRule.drainTasks(10, TimeUnit.SECONDS)
        db.close()
    }
}
