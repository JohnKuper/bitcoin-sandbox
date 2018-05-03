package com.kaizendeveloper.bitcoinsandbox

import com.kaizendeveloper.bitcoinsandbox.model.IdentityFactory
import com.kaizendeveloper.bitcoinsandbox.util.Crypto
import junit.framework.Assert.assertTrue
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.BeforeClass
import org.junit.Test
import java.security.Security
import java.security.Signature
import java.security.interfaces.ECPublicKey

class ECTest {

    @Test
    fun generateTestAddress() {
        val keyPair = IdentityFactory.generateECKeyPair()
        val pubKey = keyPair.public as ECPublicKey
        IdentityFactory.toAddress(pubKey)
    }

    @Test
    fun signatureShouldBeValid() {
        val keyPair = IdentityFactory.generateECKeyPair()
        val signature = Signature.getInstance("SHA256withECDSA").apply { initSign(keyPair.private) }
        val toSign = "BitCoin".toByteArray()
        signature.update(toSign)

        assertTrue(Crypto.verifySignature(keyPair.public, toSign, signature.sign()))
    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun beforeAll() {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
