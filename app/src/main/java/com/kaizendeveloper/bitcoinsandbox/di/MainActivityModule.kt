package com.kaizendeveloper.bitcoinsandbox.di

import com.kaizendeveloper.bitcoinsandbox.ui.view.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [FragmentsBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}
