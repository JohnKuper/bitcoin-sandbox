package com.kaizendeveloper.bitcoinsandbox.di

import com.kaizendeveloper.bitcoinsandbox.ui.BlockChainFragment
import com.kaizendeveloper.bitcoinsandbox.ui.MempoolFragment
import com.kaizendeveloper.bitcoinsandbox.ui.TransferFragment
import com.kaizendeveloper.bitcoinsandbox.ui.UsersFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentsBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeUsersFragment(): UsersFragment

    @ContributesAndroidInjector
    abstract fun contributeTransferFragment(): TransferFragment

    @ContributesAndroidInjector
    abstract fun contributeMempoolFragment(): MempoolFragment

    @ContributesAndroidInjector
    abstract fun contributeBlockchainFragment(): BlockChainFragment
}
