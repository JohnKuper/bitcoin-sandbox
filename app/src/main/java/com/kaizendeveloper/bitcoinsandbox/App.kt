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
        bootstrap()
    }

    private fun bootstrap() {
        val satoshi = UserFactory.createUser("Satoshi")
        UserFactory.createUser("Alice")
        UserFactory.createUser("Bob")

        UserFactory.activeUser = satoshi

        val tx = Transaction(25.00, satoshi.publicKey)
        val genesisBlock = Block().apply { addTransaction(tx) }
        genesisBlock.build()

        BlockChain.addBlock(genesisBlock)
    }
}