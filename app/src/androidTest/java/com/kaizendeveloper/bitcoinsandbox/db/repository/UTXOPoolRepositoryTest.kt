package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.support.test.runner.AndroidJUnit4
import com.kaizendeveloper.bitcoinsandbox.db.DbTest
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO
import com.kaizendeveloper.bitcoinsandbox.util.UserTestUtil.Companion.ALICE
import com.kaizendeveloper.bitcoinsandbox.util.UserTestUtil.Companion.SATOSHI
import com.kaizendeveloper.bitcoinsandbox.util.wrap
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class UTXOPoolRepositoryTest : DbTest() {

    @Inject
    lateinit var utxoPoolRepo: UTXOPoolRepository

    @Before
    fun setup() {
        appComponent.inject(this)
    }

    @Test
    fun updateUtxoPool() {
        val coinbaseTx = Transaction(25.00, SATOSHI.address)
        utxoPoolRepo.updatePool(coinbaseTx).subscribe()

        var utxo = UTXO(coinbaseTx.hash!!.wrap(), 0)
        var txOutput = coinbaseTx.outputs[0]

        assertEquals(hashMapOf(utxo to txOutput), utxoPoolRepo.getUtxoPool())

        val transferTx = Transaction().apply {
            addInput(coinbaseTx.hash!!, 0)
            addOutput(25.00, ALICE.address)
            build()
        }
        utxoPoolRepo.updatePool(transferTx).subscribe()

        utxo = UTXO(transferTx.hash!!.wrap(), 0)
        txOutput = transferTx.outputs[0]

        assertEquals(hashMapOf(utxo to txOutput), utxoPoolRepo.getUtxoPool())
    }
}