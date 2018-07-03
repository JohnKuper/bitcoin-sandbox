package com.kaizendeveloper.bitcoinsandbox.di

import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DaoModule {

    @Provides
    @Singleton
    fun provideUserDao(db: SandboxDatabase) = db.userDao()

    @Provides
    @Singleton
    fun provideUTXOPoolDao(db: SandboxDatabase) = db.utxoPoolDao()

    @Provides
    @Singleton
    fun provideMempoolDao(db: SandboxDatabase) = db.mempoolDao()

    @Provides
    @Singleton
    fun provideBlockchainDao(db: SandboxDatabase) = db.blockchainDao()
}