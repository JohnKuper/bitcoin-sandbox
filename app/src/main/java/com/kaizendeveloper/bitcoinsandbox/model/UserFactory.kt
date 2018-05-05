package com.kaizendeveloper.bitcoinsandbox.model

import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import org.bouncycastle.jce.interfaces.ECPublicKey

object UserFactory {

    fun createUser(name: String): User {
        val keyPair = Cipher.generateECKeyPair()
        val bitCoinPublicKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        return User(bitCoinPublicKey, keyPair.private, name)
    }
}
