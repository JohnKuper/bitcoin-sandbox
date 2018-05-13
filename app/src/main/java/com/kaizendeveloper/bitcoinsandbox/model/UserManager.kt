package com.kaizendeveloper.bitcoinsandbox.model

import com.kaizendeveloper.bitcoinsandbox.SandboxApplication
import com.kaizendeveloper.bitcoinsandbox.db.SandboxRepository
import com.kaizendeveloper.bitcoinsandbox.db.User
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXOPool
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import java.security.interfaces.ECPublicKey

object UserManager {

    private val usersRepo = SandboxRepository(SandboxApplication.application)

    fun createUser(name: String, isCurrent: Boolean = false): User {
        val keyPair = Cipher.generateECKeyPair(name)
        val bitCoinPublicKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        val user = User(name, bitCoinPublicKey.address, isCurrent)

        usersRepo.insert(user)
        return user
    }

    fun getUserBalance(user: User): Double {
        return UTXOPool.getAllTxOutputs().fold(0.0) { balance, output ->
            if (output.address == user.address) {
                balance + output.amount
            } else {
                balance
            }
        }
    }

    fun getByName(name: String) = usersRepo.getByName(name)
}
