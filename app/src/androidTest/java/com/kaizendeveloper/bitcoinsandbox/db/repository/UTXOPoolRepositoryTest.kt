package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.support.test.runner.AndroidJUnit4
import com.kaizendeveloper.bitcoinsandbox.db.DbTest
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO
import com.kaizendeveloper.bitcoinsandbox.util.ALICE
import com.kaizendeveloper.bitcoinsandbox.util.SATOSHI
import com.kaizendeveloper.bitcoinsandbox.util.wrap
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class UTXOPoolRepositoryTest : DbTest() {

    @Inject
    lateinit var utxoPoolRepository: UTXOPoolRepository

    @Before
    fun setup() {
        appComponent.inject(this)
    }

    @Test
    fun updateUtxoPool() {
        val coinbaseTx = Transaction(25.00, SATOSHI.address)
        utxoPoolRepository.updatePool(coinbaseTx).subscribe()

        var utxo = UTXO(coinbaseTx.hash!!.wrap(), 0)
        var txOutput = coinbaseTx.outputs[0]

        assertEquals(hashMapOf(utxo to txOutput), utxoPoolRepository.getUtxoPool())

        val transferTx = Transaction().apply {
            addInput(coinbaseTx.hash!!, 0)
            addOutput(25.00, ALICE.address)
            build()
        }
        utxoPoolRepository.updatePool(transferTx).subscribe()

        utxo = UTXO(transferTx.hash!!.wrap(), 0)
        txOutput = transferTx.outputs[0]

        assertEquals(hashMapOf(utxo to txOutput), utxoPoolRepository.getUtxoPool())
    }
}