package com.kaizendeveloper.bitcoinsandbox.blockchain

import android.util.Log
import com.kaizendeveloper.bitcoinsandbox.SANDBOX_TAG
import com.kaizendeveloper.bitcoinsandbox.SandboxApplication
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.transaction.TxHandler
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.decodeBits
import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import com.kaizendeveloper.bitcoinsandbox.util.toHexString
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.util.Calendar


object Miner {

    private val mempool = SandboxApplication.mempool
    private val txHandler = TxHandler()

    fun mine(recipient: User): Single<Block> {
        return if (!SandboxApplication.prefHelper.isBootstrapped() || mempool.getAll().isNotEmpty()) {
            addCoinBaseTx(recipient)

            val prevBlockHash = BlockChain.getLastHash()
            val transactions = mempool.getAll()
            val merkleRoot = MerkleRootGenerator.generate(transactions.map { it.hash!! })
            val timeStamp = Calendar.getInstance().timeInMillis

            val immutableMiningData = prepareImmutableRawMiningData(prevBlockHash, merkleRoot, timeStamp)
            var nonce = 0

            //TODO Rewrite this in more Rx style using different operators
            Single.fromCallable {
                var validHash = Cipher.maxHash

                while (BigInteger(validHash.toHexString(), 16) >= decodeBits(CURRENT_TARGET)) {
                    validHash = Cipher.sha256(immutableMiningData + nonce++.toByteArray())
                }

                Log.d(SANDBOX_TAG, "Number of cycles: $nonce")
                Log.d(SANDBOX_TAG, "Valid hash: ${validHash.toHexString()}")

                Block(validHash, prevBlockHash, merkleRoot, timeStamp, CURRENT_TARGET, nonce, transactions)
            }.subscribeOn(Schedulers.computation())
        } else {
            Single.error(IllegalStateException("Block chain is already bootstrapped and mempool is empty"))
        }
    }

    private fun addCoinBaseTx(recipient: User) {
        val coinbaseTx = Transaction(MINER_REWARD, recipient.address)
        txHandler.handleTxs(arrayOf(coinbaseTx))
    }

    private fun prepareImmutableRawMiningData(
        prevBlockHash: ByteArray,
        merkleRoot: ByteArray,
        timeStamp: Long
    ): ByteArray {
        return ByteArrayOutputStream().apply {
            write(prevBlockHash)
            write(merkleRoot)
            write(timeStamp.toByteArray())
            write(CURRENT_TARGET.toByteArray())
        }.toByteArray()
    }
}
