package com.kaizendeveloper.bitcoinsandbox.di

import android.support.test.InstrumentationRegistry
import com.kaizendeveloper.bitcoinsandbox.SandboxApplication
import com.kaizendeveloper.bitcoinsandbox.util.TrampolineSchedulerRule
import org.junit.Before
import org.junit.Rule

abstract class DaggerTest {

    @Suppress("unused")
    @get:Rule
    val trampolineSchedulerRule = TrampolineSchedulerRule()

    protected lateinit var appComponent: TestAppComponent

    @Before
    fun initComponent() {
        appComponent = DaggerTestAppComponent
            .builder()
            .application(InstrumentationRegistry.getTargetContext().applicationContext as SandboxApplication)
            .build()
    }
}