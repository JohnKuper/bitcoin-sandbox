package com.kaizendeveloper.bitcoinsandbox.transaction

import UserTestUtil.Companion.ALICE
import UserTestUtil.Companion.BOB
import UserTestUtil.Companion.SATOSHI
import android.support.test.runner.AndroidJUnit4
import com.kaizendeveloper.bitcoinsandbox.db.DbTest
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.randomByteArray
import com.kaizendeveloper.bitcoinsandbox.util.wrap
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.security.PrivateKey
import java.security.PublicKey
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class TransactionHandlerTest : DbTest() {

    @Inject
    lateinit var utxoPoolRepo: UTXOPoolRepository
    @Inject
    lateinit var transactionHandler: TransactionHandler

    private val satoshiKeyPair = Cipher.generateECKeyPair(SATOSHI.name)
    private val aliceKeyPair = Cipher.generateECKeyPair(ALICE.name)

    @Before
    fun setup() {
        appComponent.inject(this)
        utxoPoolRepo.updatePool(Transaction(25.00, SATOSHI.address)).subscribe()
    }

    @Test
    fun validTransaction() {
        val transferToAlice = Transaction().apply {
            val utxo = utxoAt(0)
            addInput(utxo.txHash.data, utxo.outputIndex)
            addOutput(10.00, ALICE.address)
            addOutput(15.00, BOB.address)

            signAllInputs(satoshiKeyPair.public, satoshiKeyPair.private)
            build()
        }

        assertTrue(transactionHandler.isValidTx(transferToAlice))
    }

    @Test
    fun tryToSpendInvalidUtxo() {
        val invalidTx = Transaction().apply {
            addInput(randomByteArray(), 0)
            addOutput(10.00, ALICE.address)

            signAllInputs(satoshiKeyPair.public, satoshiKeyPair.private)
            build()
        }

        assertFalse(transactionHandler.isValidTx(invalidTx))
    }

    @Test
    fun invalidPublicKeyForScriptSig() {
        val invalidTx = Transaction().apply {
            val utxo = utxoAt(0)
            addInput(utxo.txHash.data, utxo.outputIndex)
            addOutput(10.00, ALICE.address)

            signAllInputs(aliceKeyPair.public, satoshiKeyPair.private)
            build()
        }

        assertFalse(transactionHandler.isValidTx(invalidTx))
    }

    @Test
    fun utxoClaimedMultipleTimes() {
        val invalidTx = Transaction().apply {
            val utxo = utxoAt(0)
            addInput(utxo.txHash.data, utxo.outputIndex)
            addInput(utxo.txHash.data, utxo.outputIndex)
            addOutput(10.00, ALICE.address)

            signAllInputs(satoshiKeyPair.public, satoshiKeyPair.private)
            build()
        }

        assertFalse(transactionHandler.isValidTx(invalidTx))
    }

    @Test
    fun negativeOutputValue() {
        val invalidTx = Transaction().apply {
            val utxo = utxoAt(0)
            addInput(utxo.txHash.data, utxo.outputIndex)
            addOutput(10.00, ALICE.address)
            addOutput(-5.00, BOB.address)

            signAllInputs(satoshiKeyPair.public, satoshiKeyPair.private)
            build()
        }

        assertFalse(transactionHandler.isValidTx(invalidTx))
    }

    @Test
    fun sumOfInputsIsLowerThenSumOfOutputs() {
        val invalidTx = Transaction().apply {
            val utxo = utxoAt(0)
            addInput(utxo.txHash.data, utxo.outputIndex)
            addOutput(10.00, ALICE.address)
            addOutput(25.00, BOB.address)

            signAllInputs(satoshiKeyPair.public, satoshiKeyPair.private)
            build()
        }

        assertFalse(transactionHandler.isValidTx(invalidTx))
    }

    private fun utxoAt(keyIndex: Int) = utxoPoolRepo.getPool().keys.elementAt(keyIndex)

    private fun Transaction.signAllInputs(publicKey: PublicKey, privateKey: PrivateKey) {
        inputs.forEachIndexed { index, _ ->
            val inputSignature = Cipher.sign(getRawDataToSign(index), privateKey)
            val scriptSig = ScriptSig(inputSignature.wrap(), publicKey)
            addScriptSig(scriptSig, index)
        }
    }
}