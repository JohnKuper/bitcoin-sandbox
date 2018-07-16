package com.kaizendeveloper.bitcoinsandbox.util

import android.support.test.InstrumentationRegistry
import com.kaizendeveloper.bitcoinsandbox.SandboxApplication
import com.kaizendeveloper.bitcoinsandbox.TrampolineSchedulerRule
import com.kaizendeveloper.bitcoinsandbox.di.DaggerTestAppComponent
import com.kaizendeveloper.bitcoinsandbox.di.TestAppComponent
import org.junit.Before
import org.junit.Rule

abstract class DaggerTest {

    @Suppress("unused")
    @get:Rule
    val trampolineSchedulerRule = TrampolineSchedulerRule()

    protected lateinit var appComponent: TestAppComponent

    @Before
    fun initDb() {
        appComponent = DaggerTestAppComponent
            .builder()
            .application(InstrumentationRegistry.getTargetContext().applicationContext as SandboxApplication)
            .build()
    }
}