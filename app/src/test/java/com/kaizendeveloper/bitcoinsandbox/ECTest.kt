package com.kaizendeveloper.bitcoinsandbox

import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.model.BitCoinPublicKey
import com.kaizendeveloper.bitcoinsandbox.model.UserFactory
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.Crypto
import junit.framework.Assert.assertTrue
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.BeforeClass
import org.junit.Test
import java.security.Security

class ECTest {

    @Test
    fun generateTestAddress() {
        val keyPair = Crypto.generateECKeyPair()
        val pubKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        pubKey.address
    }

    @Test
    fun signatureShouldBeValid() {
        val keyPair = Crypto.generateECKeyPair()
        val toSign = "BitCoin".toByteArray()

        assertTrue(Crypto.verifySignature(keyPair.public, toSign, Crypto.sign(toSign, keyPair.private)))
    }

    @Test
    fun testRawDataToSign() {
        val keyPair = Crypto.generateECKeyPair()
        val bitCoinPubKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        val transaction = Transaction()
        transaction.addInput("prevTxHash".toByteArray(), 0)
        transaction.addOutput(10.00, bitCoinPubKey)

        val expected = transaction.getRawDataToSign(0)
    }

    @Test
    fun testRawTx() {
        val keyPair = Crypto.generateECKeyPair()
        val bitCoinPubKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        val transaction = Transaction()
        transaction.addInput("prevTxHash".toByteArray(), 0)
        transaction.addInput("prevTxHash2".toByteArray(), 1)
        transaction.addOutput(10.00, bitCoinPubKey)
        transaction.addOutput(15.00, bitCoinPubKey)

        val firstRawToSign = transaction.getRawDataToSign(0)
        val secondRawToSign = transaction.getRawDataToSign(1)
        transaction.addSignature(Crypto.sign(firstRawToSign, keyPair.private), 0)
        transaction.addSignature(Crypto.sign(secondRawToSign, keyPair.private), 1)

        val block = Block("prevBlockHash".toByteArray())
        block.addTransaction(transaction)
    }

    @Test
    fun genesisTransaction() {
        val user = UserFactory.createUser("Satoshi")
        val tx = Transaction(25.00, user.publicKey)
        val genesisBlock = Block().apply { addTransaction(tx) }


    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun beforeAll() {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
