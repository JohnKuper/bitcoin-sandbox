package com.kaizendeveloper.bitcoinsandbox.di

import com.kaizendeveloper.bitcoinsandbox.ui.view.BlockchainFragment
import com.kaizendeveloper.bitcoinsandbox.ui.view.MempoolFragment
import com.kaizendeveloper.bitcoinsandbox.ui.view.TransferFragment
import com.kaizendeveloper.bitcoinsandbox.ui.view.UsersFragment
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
    abstract fun contributeBlockchainFragment(): BlockchainFragment
}
