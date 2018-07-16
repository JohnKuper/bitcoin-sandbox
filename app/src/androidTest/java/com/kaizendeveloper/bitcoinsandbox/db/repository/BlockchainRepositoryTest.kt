package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.support.test.runner.AndroidJUnit4
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.blockchain.CURRENT_TARGET
import com.kaizendeveloper.bitcoinsandbox.db.DbTest
import com.kaizendeveloper.bitcoinsandbox.db.dao.BlockchainDao
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.generateMockTransactions
import com.kaizendeveloper.bitcoinsandbox.util.randomByteArray
import com.kaizendeveloper.bitcoinsandbox.util.requireValue
import junit.framework.Assert.assertEquals
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class BlockchainRepositoryTest : DbTest() {

    @Inject
    lateinit var blockchainRepo: BlockchainRepository
    @Inject
    lateinit var blockchainDao: BlockchainDao
    @Inject
    lateinit var mempoolRepo: MempoolRepository

    @Before
    fun setup() {
        appComponent.inject(this)
    }

    @Test
    fun insertBlock() {
        val mockTransactions = generateMockTransactions().also {
            it.forEach {
                mempoolRepo.insert(it).subscribe()
            }
        }
        val block = Block(
            hash = randomByteArray(),
            prevBlockHash = randomByteArray(),
            merkleRoot = randomByteArray(),
            timestamp = System.currentTimeMillis(),
            target = CURRENT_TARGET,
            nonce = 34534,
            transactions = mockTransactions
        )

        assertEquals(Cipher.ZERO_HASH, blockchainRepo.getLastHash())

        blockchainRepo.insert(block).subscribe()
        mockTransactions.forEach { it.isConfirmed = true }

        val firstDbBlock = blockchainDao.getAll().requireValue().map { it.toBlock() }[0]

        assertEquals(block, firstDbBlock)
        assertArrayEquals(block.hash, blockchainRepo.getLastHash())
    }
}