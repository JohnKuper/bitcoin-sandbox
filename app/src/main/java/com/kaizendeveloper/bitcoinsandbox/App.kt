package com.kaizendeveloper.bitcoinsandbox

import android.app.Application
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.blockchain.BlockChain
import com.kaizendeveloper.bitcoinsandbox.model.UserFactory
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Security.addProvider(BouncyCastleProvider())
    }

    private fun bootstrap() {
        val satoshi = UserFactory.createUser("Satoshi")
        UserFactory.createUser("Alice")

        val tx = Transaction(25.00, satoshi.publicKey)
        val genesisBlock = Block().apply { addTransaction(tx) }
        BlockChain.addBlock(genesisBlock)
    }
}