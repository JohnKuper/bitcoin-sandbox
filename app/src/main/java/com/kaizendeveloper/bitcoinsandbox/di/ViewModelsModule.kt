package com.kaizendeveloper.bitcoinsandbox.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kaizendeveloper.bitcoinsandbox.ui.TransactionsViewModel
import com.kaizendeveloper.bitcoinsandbox.ui.UsersViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(UsersViewModel::class)
    abstract fun bindUsersViewModel(usersViewModel: UsersViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransactionsViewModel::class)
    abstract fun bindTransactionsViewModel(transactionsViewModel: TransactionsViewModel): ViewModel

    @Binds
    abstract fun bindSandboxViewModelFactory(factory: SandboxViewModelFactory): ViewModelProvider.Factory
}
