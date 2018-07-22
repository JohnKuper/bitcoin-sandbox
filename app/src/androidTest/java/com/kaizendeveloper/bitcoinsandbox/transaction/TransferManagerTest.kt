package com.kaizendeveloper.bitcoinsandbox.transaction

import ALICE
import SATOSHI
import android.support.test.runner.AndroidJUnit4
import calculateBalance
import com.kaizendeveloper.bitcoinsandbox.db.DbTest
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class TransferManagerTest : DbTest() {

    @Inject
    lateinit var transferManager: TransferManager
    @Inject
    lateinit var utxoPoolRepo: UTXOPoolRepository

    @Before
    fun setup() {
        appComponent.inject(this)
        Cipher.generateECKeyPair(SATOSHI.name)
    }

    @Test
    fun notEnoughCoinsShouldThrowException() {
        transferManager
            .sendCoins(10.00, SATOSHI, ALICE)
            .test()
            .assertError(IllegalStateException::class.java)
    }

    @Test
    fun coinsShouldBeSentAndExtraAmountShouldBeSentToSender() {
        val initialAmount = 25.00
        utxoPoolRepo
            .updatePool(Transaction(initialAmount, SATOSHI.address))
            .subscribe()
        val satoshi = SATOSHI.copy().apply { balance = initialAmount }

        val toSendAmount = 15.00
        transferManager
            .sendCoins(toSendAmount, satoshi, ALICE)
            .subscribe()

        val utxoOutputs = utxoPoolRepo.getPool().values.toList()
        val actualSatoshiBalance = calculateBalance(satoshi, utxoOutputs)
        val actualAliceBalance = calculateBalance(ALICE, utxoOutputs)

        assertEquals(initialAmount - toSendAmount, actualSatoshiBalance, 0.0)
        assertEquals(toSendAmount, actualAliceBalance, 0.0)
        assertEquals(2, utxoOutputs.size)
    }

    @Test
    fun smallUnspentOutputsShouldBeUsedFirst() {
        val amounts = listOf(1.00, 1.5, 2.0, 3.5, 4.5, 6.0, 10.00, 25.00)
        Observable.fromIterable(amounts)
            .flatMapCompletable {
                utxoPoolRepo.updatePool(Transaction(it, SATOSHI.address))
            }.subscribe()

        val satoshi = SATOSHI.copy().apply { balance = amounts.sum() }
        transferManager.sendCoins(18.5, satoshi, ALICE).subscribe()

        val utxoOutputs = utxoPoolRepo.getPool().values.toList()
        assertEquals(2, utxoOutputs.count { it.address == satoshi.address })
        assertEquals(35.00, calculateBalance(satoshi, utxoOutputs), 0.0)
        assertEquals(18.5, calculateBalance(ALICE, utxoOutputs), 0.0)
    }
}