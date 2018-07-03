package com.kaizendeveloper.bitcoinsandbox.db

import android.support.test.runner.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MempoolDaoTest : DbTest() {

//    private lateinit var mempoolRepo: MempoolRepository
//
//    @Before
//    fun setup() {
//        mempoolRepo = MempoolRepository(InstrumentationRegistry.getContext(), db)
//    }
//
//    @Test
//    fun insertAndGetTransaction() {
//        //TODO Implement TransactionGenerator which accepts number of inputs and outputs. All values can be random.
//        val keyPair = Cipher.generateECKeyPair("test")
//        val transaction1 = Transaction().apply {
//            addInput("prev_1".toByteArray(), 0)
//            addInput("prev_2".toByteArray(), 1)
//            addScriptSig(ScriptSig("signature".toByteArray().wrap(), keyPair.public), 0)
//            addScriptSig(ScriptSig("signature2".toByteArray().wrap(), keyPair.public), 1)
//            addOutput(15.55, "alice_address")
//            addOutput(5.00, "bob_address")
//            build()
//        }
//        val transaction2 = Transaction().apply {
//            addInput("prev_3".toByteArray(), 0)
//            addOutput(10.00, "satoshi_address")
//            build()
//        }
//
//        val transactions = listOf(transaction1, transaction2)
//        mempoolRepo.insert(transaction1)
//        mempoolRepo.insert(transaction2)
//
//        val dbTransactions = getValue(mempoolRepo.transactions)
//        assertTrue(transactions == dbTransactions)
//
//
//        transaction1.isConfirmed = true
//        transaction2.isConfirmed = true
//        val block = Block(
//            "block".toByteArray(),
//            "prev".toByteArray(),
//            "merkle_root".toByteArray(),
//            System.currentTimeMillis(),
//            CURRENT_TARGET,
//            45343423,
//            transactions
//        )
//
//        mempoolRepo.insert(block)
//    }
}
