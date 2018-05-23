package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.transaction.TxHandler
import java.util.LinkedList
import java.util.Observable

object BlockChain : Observable() {

    val blocks = LinkedList<Block>()
    private val txHandler = TxHandler()

    fun addBlock(block: Block) {
        blocks.add(block)

        setChanged()
        notifyObservers()
    }
}
