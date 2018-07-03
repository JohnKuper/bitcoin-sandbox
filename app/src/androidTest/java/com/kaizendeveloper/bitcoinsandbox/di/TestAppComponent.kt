package com.kaizendeveloper.bitcoinsandbox.di

import android.app.Application
import com.kaizendeveloper.bitcoinsandbox.db.repository.UsersRepositoryTest
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
}