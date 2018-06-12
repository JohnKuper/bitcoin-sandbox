package com.kaizendeveloper.bitcoinsandbox

import com.kaizendeveloper.bitcoinsandbox.blockchain.MerkleRootGenerator
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.UUID


class MerkleRootGeneratorTest {

    private fun generateMockTxsHashes(count: Int): List<ByteArray> {
        return arrayListOf<ByteArray>().apply {
            repeat(count) {
                val tx = Transaction(10.00, UUID.randomUUID().toString())
                add(tx.hash!!)
            }
        }
    }

    @Test
    fun rootShouldBeGeneratedForOddAmount() {
        val merkleRoot = MerkleRootGenerator.generate(generateMockTxsHashes(9))
        assertNotNull(merkleRoot)
    }

    @Test
    fun rootShouldBeGeneratedForPowOf2Amount() {
        val merkleRoot = MerkleRootGenerator.generate(generateMockTxsHashes(16))
        assertNotNull(merkleRoot)
    }

    @Test
    fun rootShouldBeDeterministic() {
        val hashes = generateMockTxsHashes(10)
        val firstRoot = MerkleRootGenerator.generate(hashes)
        val secondRoot = MerkleRootGenerator.generate(hashes)

        assertArrayEquals(firstRoot, secondRoot)
    }
}
