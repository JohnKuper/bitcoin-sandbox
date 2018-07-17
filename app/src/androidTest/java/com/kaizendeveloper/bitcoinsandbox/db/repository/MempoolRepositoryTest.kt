package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.support.test.runner.AndroidJUnit4
import com.kaizendeveloper.bitcoinsandbox.db.DbTest
import com.kaizendeveloper.bitcoinsandbox.db.dao.MempoolDao
import com.kaizendeveloper.bitcoinsandbox.transaction.ScriptSig
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.UserTestUtil.Companion.ALICE
import com.kaizendeveloper.bitcoinsandbox.util.UserTestUtil.Companion.SATOSHI
import com.kaizendeveloper.bitcoinsandbox.util.randomByteArray
import com.kaizendeveloper.bitcoinsandbox.util.requireValue
import com.kaizendeveloper.bitcoinsandbox.util.wrap
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class MempoolRepositoryTest : DbTest() {

    @Inject
    lateinit var mempoolRepo: MempoolRepository
    @Inject
    lateinit var mempoolDao: MempoolDao

    @Before
    fun setup() {
        appComponent.inject(this)
    }

    @Test
    fun insertTransaction() {
        val satoshiKeyPair = Cipher.generateECKeyPair(SATOSHI.name)
        val aliceKeyPair = Cipher.generateECKeyPair(ALICE.name)

        val transaction = Transaction()
        val satoshiScriptSig = ScriptSig(randomByteArray().wrap(), satoshiKeyPair.public)
        val aliceScriptSig = ScriptSig(randomByteArray().wrap(), aliceKeyPair.public)

        with(transaction) {
            addInput(randomByteArray(), 0)
            addInput(randomByteArray(), 1)
            addOutput(10.00, SATOSHI.address)
            addOutput(15.55, ALICE.address)
            addScriptSig(satoshiScriptSig, 0)
            addScriptSig(aliceScriptSig, 1)
            build()
        }

        mempoolRepo.insert(transaction).blockingAwait()

        val dbTransaction = mempoolDao.getAll().requireValue()[0]
        val unconfirmedTx = mempoolRepo.getAllUnconfirmed().blockingGet()[0]

        assertEquals(transaction, dbTransaction.toTransaction())
        assertEquals(transaction, unconfirmedTx)
    }
}