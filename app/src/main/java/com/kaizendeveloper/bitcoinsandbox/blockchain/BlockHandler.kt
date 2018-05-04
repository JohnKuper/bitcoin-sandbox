package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.transaction.TxHandler
import java.security.PublicKey

class BlockHandler
/**
 * assume blockChain has the genesis block
 */
    (private val blockChain: BlockChain2) {

    /**
     * add `block` to the block chain if it is valid.
     *
     * @return true if the block is valid and has been added, false otherwise
     */
    fun processBlock(block: Block?): Boolean {
        return if (block == null) false else blockChain.addBlock(block)
    }

    /**
     * create a new `block` over the max height `block`
     */
    fun createBlock(myAddress: PublicKey): Block? {
        val parent = blockChain.maxHeightBlock
        val parentHash = parent.hash
        val newBlock = Block(parentHash)
        val uPool = blockChain.maxHeightUTXOPool
        val txPool = blockChain.transactionPool
        val handler = TxHandler()
        val txs = txPool.transactions.toTypedArray()
        val correctTxs = handler.handleTxs(txs)

        for (correctTx in correctTxs) {
            newBlock.addTransaction(correctTx)
        }

        newBlock.build()
        return if (blockChain.addBlock(newBlock))
            newBlock
        else
            null
    }

    /**
     * process a `Transaction`
     */
    fun processTx(tx: Transaction) {
        blockChain.addTransaction(tx)
    }
}
