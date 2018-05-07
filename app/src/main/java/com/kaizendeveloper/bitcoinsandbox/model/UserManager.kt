package com.kaizendeveloper.bitcoinsandbox.model

import com.kaizendeveloper.bitcoinsandbox.transaction.UTXOPool
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import java.security.interfaces.ECPublicKey

object UserManager {

    val users = arrayListOf<User>()
    lateinit var activeUser: User

    fun createUser(name: String): User {
        val keyPair = Cipher.generateECKeyPair()
        val bitCoinPublicKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        val user = User(bitCoinPublicKey, keyPair.private, name)
        users.add(user)

        return user
    }

    fun getUserBalance(): Double {
        return UTXOPool.getAllTxOutputs().fold(0.0) { balance, output ->
            if (output.bitCoinPublicKey.address == activeUser.publicKey.address) {
                balance + output.amount
            } else {
                balance
            }
        }
    }
}
