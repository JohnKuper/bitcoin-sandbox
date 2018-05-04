package com.kaizendeveloper.bitcoinsandbox.blockchain

import java.util.LinkedList

object BlockChain {

    private val blockChain = LinkedList<Block>()

    fun addBlock(block: Block) {
        //verify
        blockChain.add(block)
    }
}
