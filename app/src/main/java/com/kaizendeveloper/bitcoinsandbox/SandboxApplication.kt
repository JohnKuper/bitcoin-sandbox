package com.kaizendeveloper.bitcoinsandbox

import android.app.Application
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.blockchain.BlockChain
import com.kaizendeveloper.bitcoinsandbox.model.UserManager
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class SandboxApplication : Application() {

    init {
        application = this
    }

    override fun onCreate() {
        super.onCreate()
        Security.addProvider(BouncyCastleProvider())
        bootstrap()
    }

    private fun bootstrap() {
        val satoshi = UserManager.createUser("Satoshi")
//        UserManager.createUser("Alice")
//        UserManager.createUser("Bob")

        UserManager.activeUser = satoshi

        val tx = Transaction(25.00, satoshi.publicKey!!)
        val genesisBlock = Block().apply { addTransaction(tx) }
        genesisBlock.build()

        BlockChain.addBlock(genesisBlock)
    }

    companion object {
        lateinit var application: SandboxApplication
    }
}
