package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.transaction.Mempool
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import java.util.LinkedList

object BlockChain {

    private val blocks = LinkedList<Block>()

    //TODO add necessary checks for correct block
    fun processBlock(block: Block) {
        addBlock(block)
        Mempool.reset()
    }

    fun getLastHash(): ByteArray {
        return if (blocks.isNotEmpty()) {
            blocks.last.hash
        } else {
            Cipher.zeroHash
        }
    }

    private fun addBlock(block: Block) {
        blocks.add(block)
    }
}
