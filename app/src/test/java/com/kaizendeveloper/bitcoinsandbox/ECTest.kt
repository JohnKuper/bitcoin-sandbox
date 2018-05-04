package com.kaizendeveloper.bitcoinsandbox

import com.kaizendeveloper.bitcoinsandbox.model.IdentityFactory
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.Crypto
import junit.framework.Assert.assertTrue
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.BeforeClass
import org.junit.Test
import java.security.Security
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
        val toSign = "BitCoin".toByteArray()

        assertTrue(Crypto.verifySignature(keyPair.public, toSign, Crypto.sign(toSign, keyPair.private)))
    }

    @Test
    fun testRawDataToSign() {
        val keyPair = IdentityFactory.generateECKeyPair()
        val transaction = Transaction()
        transaction.addInput("prevTxHash".toByteArray(), 0)
        transaction.addOutput(10.00, keyPair.public as ECPublicKey)

        val expected = transaction.getRawDataToSign(0)
    }

    @Test
    fun testRawTx() {
        val keyPair = IdentityFactory.generateECKeyPair()
        val transaction = Transaction()
        transaction.addInput("prevTxHash".toByteArray(), 0)
        transaction.addInput("prevTxHash2".toByteArray(), 1)
        transaction.addOutput(10.00, keyPair.public as ECPublicKey)
        transaction.addOutput(15.00, keyPair.public as ECPublicKey)

        val firstRawToSign = transaction.getRawDataToSign(0)
        val secondRawToSign = transaction.getRawDataToSign(1)
        transaction.addSignature(Crypto.sign(firstRawToSign, keyPair.private), 0)
        transaction.addSignature(Crypto.sign(secondRawToSign, keyPair.private), 1)
    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun beforeAll() {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
