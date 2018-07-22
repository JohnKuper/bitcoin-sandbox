package com.kaizendeveloper.bitcoinsandbox.di

import android.app.Application
import com.kaizendeveloper.bitcoinsandbox.db.repository.BlockchainRepositoryTest
import com.kaizendeveloper.bitcoinsandbox.db.repository.MempoolRepositoryTest
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepositoryTest
import com.kaizendeveloper.bitcoinsandbox.db.repository.UsersRepositoryTest
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionHandlerTest
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        TestAppModule::class
    ]
)
interface TestAppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): TestAppComponent
    }

    fun inject(usersRepo: UsersRepositoryTest)
    fun inject(mempoolRepo: MempoolRepositoryTest)
    fun inject(utxoPoolRepo: UTXOPoolRepositoryTest)
    fun inject(blockchainRepo: BlockchainRepositoryTest)
    fun inject(transactionHandler: TransactionHandlerTest)
}