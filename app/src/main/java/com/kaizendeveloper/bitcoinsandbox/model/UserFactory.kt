package com.kaizendeveloper.bitcoinsandbox.model

import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import java.security.interfaces.ECPublicKey

object UserFactory {

    val users = arrayListOf<User>()
    var activeUser: User? = null

    fun createUser(name: String): User {
        val keyPair = Cipher.generateECKeyPair()
        val bitCoinPublicKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        val user = User(bitCoinPublicKey, keyPair.private, name)
        users.add(user)

        return user
    }
}
