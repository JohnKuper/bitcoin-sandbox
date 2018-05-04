package com.kaizendeveloper.bitcoinsandbox.model

import com.kaizendeveloper.bitcoinsandbox.util.Crypto
import org.bouncycastle.jce.interfaces.ECPublicKey

object UserFactory {

    fun createUser(name: String): User {
        val keyPair = Crypto.generateECKeyPair()
        val bitCoinPublicKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        return User(bitCoinPublicKey, keyPair.private, name)
    }
}
