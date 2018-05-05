package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.transaction.TxHandler
import java.util.LinkedList

object BlockChain {

    private val blockChain = LinkedList<Block>()
    private val txHandler = TxHandler()

    fun addBlock(block: Block) {
        txHandler.handleTxs(block.transactions.toTypedArray())
        blockChain.add(block)
    }
}
