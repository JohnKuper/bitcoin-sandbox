package com.kaizendeveloper.bitcoinsandbox.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DaoModule::class])
class TestAppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): SandboxDatabase {
        return Room.inMemoryDatabaseBuilder(
            context.applicationContext,
            SandboxDatabase::class.java
        ).allowMainThreadQueries().build()
    }
}