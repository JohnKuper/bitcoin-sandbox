package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.transaction.Mempool
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.decodeBits
import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import com.kaizendeveloper.bitcoinsandbox.util.toHexString
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.math.BigInteger
import java.util.Calendar


object Miner {

    fun mine(): Single<Block> {
        val prevBlockHash = Cipher.sha256("prevHash".toByteArray())
        val hashes = Mempool.getAll().map { it.hash!! }
        val merkleRoot = MerkleRootGenerator.generate(hashes)
        val timestamp = Calendar.getInstance().timeInMillis

        var nonce = 0
        return Single.fromCallable {
            var validHash = ByteArray(32, { 0xFF.toByte() })
            val dataToHash = prevBlockHash + merkleRoot
            while (BigInteger(validHash.toHexString(), 16) >= decodeBits(CURRENT_TARGET)) {
                validHash = Cipher.sha256(dataToHash + nonce++.toByteArray())
            }
            println("Number of cycles: $nonce")
            println("Valid hash: ${validHash.toHexString()}")
            Block(validHash, prevBlockHash, merkleRoot, timestamp, nonce)
        }.subscribeOn(Schedulers.computation())
    }
}
