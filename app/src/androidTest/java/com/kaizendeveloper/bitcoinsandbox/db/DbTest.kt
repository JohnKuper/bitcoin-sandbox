package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.core.executor.testing.CountingTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.TimeUnit

abstract class DbTest {

    @Rule
    @JvmField
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    protected lateinit var db: SandboxDatabase

    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getContext(),
            SandboxDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() {
        countingTaskExecutorRule.drainTasks(10, TimeUnit.SECONDS)
        db.close()
    }
}
