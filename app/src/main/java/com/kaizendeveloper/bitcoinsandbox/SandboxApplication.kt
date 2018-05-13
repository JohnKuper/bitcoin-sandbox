package com.kaizendeveloper.bitcoinsandbox

import android.app.Application
import com.facebook.stetho.Stetho
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.blockchain.BlockChain
import com.kaizendeveloper.bitcoinsandbox.model.UserManager
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.SharedPreferencesHelper
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class SandboxApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        Security.addProvider(BouncyCastleProvider())

        application = this
        prefHelper = SharedPreferencesHelper.getInstance(this)

        if (!prefHelper.isBootstrapped()) {
            bootstrapBlockChain()
            prefHelper.setBootstrapped()
        }
    }

    private fun bootstrapBlockChain() {
        val satoshi = UserManager.createUser("Satoshi", true)

        val tx = Transaction(25.00, satoshi.address)
        val genesisBlock = Block().apply { addTransaction(tx) }
        genesisBlock.build()

        BlockChain.addBlock(genesisBlock)
    }

    companion object {
        lateinit var application: SandboxApplication
        lateinit var prefHelper: SharedPreferencesHelper
    }
}
