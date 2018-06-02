package com.kaizendeveloper.bitcoinsandbox.db

import android.support.test.runner.AndroidJUnit4
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UTXOPoolDaoTest : DbTest() {

    private val utxoPool = mutableListOf<UTXOWithTxOutput>()

    @Before
    fun setup() {
        for (i in 1..11) {
            val utxo = UTXO("hash $i".toByteArray(), i)
            val txOutput = TransactionOutput(10.00 + i, "some_address_$i")
            utxoPool.add(UTXOWithTxOutput(utxo, txOutput).apply { id = i })
        }
    }

    @After
    fun tearDown() {
        utxoPool.clear()
    }

    @Test
    fun insertAndGetAll() {
        populateDb()

        assertTrue(utxoPool == db.utxoPoolDao().getAll())
    }

    @Test
    fun insertAndDelete() {
        populateDb()

        for (i in 0..5) {
            val utxo = utxoPool[0].utxo
            db.utxoPoolDao().delete(utxo.txHash, utxo.outputIndex)
            utxoPool.removeAt(0)
        }

        assertTrue(utxoPool == db.utxoPoolDao().getAll())
    }

    private fun populateDb() {
        utxoPool.forEach {
            db.utxoPoolDao().insert(it)
        }
    }
}