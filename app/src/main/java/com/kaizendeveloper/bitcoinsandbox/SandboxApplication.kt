package com.kaizendeveloper.bitcoinsandbox

import android.app.Activity
import android.app.Application
import android.util.Log
import com.facebook.stetho.Stetho
import com.kaizendeveloper.bitcoinsandbox.blockchain.Miner
import com.kaizendeveloper.bitcoinsandbox.db.repository.BlockchainRepository
import com.kaizendeveloper.bitcoinsandbox.di.DaggerAppComponent
import com.kaizendeveloper.bitcoinsandbox.model.UserManager
import com.kaizendeveloper.bitcoinsandbox.util.SharedPreferencesHelper
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.inject.Inject

const val SANDBOX_TAG = "BitcoinSandbox"

class SandboxApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var userManager: UserManager
    @Inject
    lateinit var miner: Miner
    @Inject
    lateinit var blockchainRepo: BlockchainRepository

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent
            .builder()
            .application(this)
            .build()
            .inject(this)

        Stetho.initializeWithDefaults(this)
        Security.addProvider(BouncyCastleProvider())

        application = this
        prefHelper = SharedPreferencesHelper.getInstance(this)

        if (!prefHelper.isBootstrapped()) {
            bootstrapBlockChain()
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    private fun bootstrapBlockChain() {
        userManager
            .createUserIfAbsent("Satoshi", true)
            .flatMap { user ->
                miner.mine(user)
            }.subscribe { block ->
                Log.d(SANDBOX_TAG, "Genesis block has been created")
                blockchainRepo.insert(block)
                prefHelper.setBootstrapped()
            }

        userManager.createUserIfAbsent("Alice", false).subscribe()
        userManager.createUserIfAbsent("Bob", false).subscribe()
    }

    companion object {
        lateinit var application: SandboxApplication
        lateinit var prefHelper: SharedPreferencesHelper
    }
}
