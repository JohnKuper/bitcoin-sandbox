package com.kaizendeveloper.bitcoinsandbox

import android.app.Activity
import android.app.Application
import android.widget.Toast
import com.kaizendeveloper.bitcoinsandbox.blockchain.Miner
import com.kaizendeveloper.bitcoinsandbox.db.repository.UsersRepository
import com.kaizendeveloper.bitcoinsandbox.di.DaggerAppComponent
import com.kaizendeveloper.bitcoinsandbox.util.SharedPreferencesHelper
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.inject.Inject

class SandboxApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var usersRepo: UsersRepository
    @Inject
    lateinit var miner: Miner

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent
            .builder()
            .application(this)
            .build()
            .inject(this)

        Security.addProvider(BouncyCastleProvider())

        application = this
        prefHelper = SharedPreferencesHelper.getInstance(this)

        if (!prefHelper.isBootstrapped()) {
            bootstrapBlockChain()
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    private fun bootstrapBlockChain() {
        Single.concat(
            usersRepo.createIfAbsent("Satoshi", true),
            usersRepo.createIfAbsent("Alice", false),
            usersRepo.createIfAbsent("Bob", false)
        )
            .filter { it.isCurrent }
            .flatMapCompletable { user ->
                miner.mine(user)
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(this, "Genesis block has been created", Toast.LENGTH_LONG).show()
                prefHelper.setBootstrapped(true)
            }
    }

    companion object {
        lateinit var application: SandboxApplication
        lateinit var prefHelper: SharedPreferencesHelper
    }
}
