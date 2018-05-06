package com.kaizendeveloper.bitcoinsandbox

import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.blockchain.BlockChain
import com.kaizendeveloper.bitcoinsandbox.model.BitCoinPublicKey
import com.kaizendeveloper.bitcoinsandbox.model.UserFactory
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXOPool
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.BeforeClass
import org.junit.Test
import java.security.Security
import java.security.interfaces.ECPublicKey
import java.util.Arrays

class ECTest {

    @Test
    fun generateTestAddress() {
        val keyPair = Cipher.generateECKeyPair()
        val pubKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        pubKey.address
    }

    @Test
    fun signatureShouldBeValid() {
        val keyPair = Cipher.generateECKeyPair()
        val toSign = "BitCoin".toByteArray()

        assertTrue(Cipher.verifySignature(keyPair.public, toSign, Cipher.sign(toSign, keyPair.private)))
    }

    @Test
    fun testRawDataToSign() {
        val keyPair = Cipher.generateECKeyPair()
        val bitCoinPubKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        val transaction = Transaction()
        transaction.addInput("prevTxHash".toByteArray(), 0)
        transaction.addOutput(10.00, bitCoinPubKey)

        val expected = transaction.getRawDataToSign(0)
    }

    @Test
    fun testRawTx() {
        val keyPair = Cipher.generateECKeyPair()
        val bitCoinPubKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        val transaction = Transaction()
        transaction.addInput("prevTxHash".toByteArray(), 0)
        transaction.addInput("prevTxHash2".toByteArray(), 1)
        transaction.addOutput(10.00, bitCoinPubKey)
        transaction.addOutput(15.00, bitCoinPubKey)

        val firstRawToSign = transaction.getRawDataToSign(0)
        val secondRawToSign = transaction.getRawDataToSign(1)
        transaction.addSignature(Cipher.sign(firstRawToSign, keyPair.private), 0)
        transaction.addSignature(Cipher.sign(secondRawToSign, keyPair.private), 1)

        val block = Block("prevBlockHash".toByteArray())
        block.addTransaction(transaction)
    }

    @Test
    fun testByteArrayOutputStream() {
        val keyPair = Cipher.generateECKeyPair()
        val bitCoinPubKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        val transaction = Transaction()
        transaction.addInput("prevTxHash".toByteArray(), 0)
        transaction.addOutput(10.00, bitCoinPubKey)
        val rawToSign = transaction.getRawDataToSign(0)
        transaction.addSignature(Cipher.sign(rawToSign, keyPair.private), 0)

        assertTrue(Arrays.equals(transaction.getRawTx(), transaction.getRawTx2()))
    }

    @Test
    fun genesisTransaction() {
        val satoshi = UserFactory.createUser("Satoshi")
        val tx = Transaction(25.00, satoshi.publicKey)
        val genesisBlock = Block().apply { addTransaction(tx) }
        BlockChain.addBlock(genesisBlock)

        assertTrue(UTXOPool.size() == 1)
        assertNotNull(UTXOPool.get(UTXO(tx.hash!!, 0)))

        val alice = UserFactory.createUser("Alice")
        val toAliceTx = Transaction()
        toAliceTx.addInput(tx.hash!!, 0)
        toAliceTx.addOutput(12.00, alice.publicKey)
        toAliceTx.addOutput(13.00, satoshi.publicKey)

        val sig = Cipher.sign(toAliceTx.getRawDataToSign(0), satoshi.privateKey)
        toAliceTx.addSignature(sig, 0)
        toAliceTx.build()
        BlockChain.addBlock(Block().apply {
            addTransaction(toAliceTx)
        })

        assertTrue(UTXOPool.size() == 2)
        assertNotNull(UTXOPool.get(UTXO(toAliceTx.hash!!, 0)))
        assertNotNull(UTXOPool.get(UTXO(toAliceTx.hash!!, 1)))
    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun beforeAll() {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
