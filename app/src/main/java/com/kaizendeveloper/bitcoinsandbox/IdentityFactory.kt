package com.kaizendeveloper.bitcoinsandbox

import java.security.KeyPairGenerator

object IdentityFactory {

    fun createIdentity(pseudonym: String): Identity {
        val generator = KeyPairGenerator.getInstance("RSA")
        val keyPair = generator.generateKeyPair()
        return Identity(keyPair.public, keyPair.private, pseudonym)
    }
}
