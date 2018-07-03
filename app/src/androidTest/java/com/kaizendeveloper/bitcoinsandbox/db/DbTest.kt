package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.core.executor.testing.CountingTaskExecutorRule
import android.support.test.InstrumentationRegistry
import com.kaizendeveloper.bitcoinsandbox.SandboxApplication
import com.kaizendeveloper.bitcoinsandbox.TrampolineSchedulerRule
import com.kaizendeveloper.bitcoinsandbox.di.DaggerTestAppComponent
import com.kaizendeveloper.bitcoinsandbox.di.TestAppComponent
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class DbTest {

    @get:Rule
    private val countingTaskExecutorRule = CountingTaskExecutorRule()
    @Suppress("unused")
    @get:Rule
    val trampolineSchedulerRule = TrampolineSchedulerRule()

    protected lateinit var appComponent: TestAppComponent

    @Inject
    protected lateinit var db: SandboxDatabase

    @Before
    fun initDb() {
        appComponent = DaggerTestAppComponent
            .builder()
            .application(InstrumentationRegistry.getTargetContext().applicationContext as SandboxApplication)
            .build()
    }

    @After
    fun closeDb() {
        countingTaskExecutorRule.drainTasks(10, TimeUnit.SECONDS)
        db.close()
    }
}
