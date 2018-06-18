package com.kaizendeveloper.bitcoinsandbox

import android.app.Application
import android.util.Log
import com.facebook.stetho.Stetho
import com.kaizendeveloper.bitcoinsandbox.blockchain.Miner
import com.kaizendeveloper.bitcoinsandbox.db.repository.MempoolRepository
import com.kaizendeveloper.bitcoinsandbox.model.UserManager
import com.kaizendeveloper.bitcoinsandbox.transaction.Mempool
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXOPool
import com.kaizendeveloper.bitcoinsandbox.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

const val SANDBOX_TAG = "BitcoinSandbox"

class SandboxApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        Security.addProvider(BouncyCastleProvider())

        application = this
        prefHelper = SharedPreferencesHelper.getInstance(this)
        utxoPool = UTXOPool(this)
        mempoolRepo = MempoolRepository(this)
        mempool = Mempool(mempoolRepo)

        if (!prefHelper.isBootstrapped()) {
            bootstrapBlockChain()
        }
    }

    private fun bootstrapBlockChain() {
        val satoshi = UserManager.createUser("Satoshi", true)
        UserManager.createUser("Alice", false)
        UserManager.createUser("Bob", false)

        Miner.mine(satoshi)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { block ->
                Log.d(SANDBOX_TAG, "Genesis block has been created")
                mempoolRepo.insert(block)
                prefHelper.setBootstrapped()
            }
    }

    companion object {
        lateinit var application: SandboxApplication
        lateinit var prefHelper: SharedPreferencesHelper
        lateinit var utxoPool: UTXOPool
        lateinit var mempool: Mempool
        lateinit var mempoolRepo: MempoolRepository
    }
}
