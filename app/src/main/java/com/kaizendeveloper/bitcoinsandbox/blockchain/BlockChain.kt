package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionPool
import com.kaizendeveloper.bitcoinsandbox.transaction.TxHandler
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXOPool
import com.kaizendeveloper.bitcoinsandbox.util.ByteArrayWrapper
import java.util.LinkedList
import java.util.Observable


class BlockChain(var maxHeightBlock: Block) : Observable() {

    /**
     * Get the transaction pool to mine a new block
     */
    var transactionPool = TransactionPool()
    var blocksToUTXOPoolsMap: HashMap<ByteArrayWrapper, UTXOPool> = HashMap()
    var head: Node

    var maxHeight: Int = 0

    /**
     * Get the UTXOPool for mining a new block on top of max height block
     */
    val maxHeightUTXOPool: UTXOPool
        get() = blocksToUTXOPoolsMap[ByteArrayWrapper(maxHeightBlock.hash!!)]!!

    val blocks: ArrayList<Block> = arrayListOf()

    init {
        maxHeight = 1
        head = Node(maxHeightBlock, 1)

        val utxo = UTXO(maxHeightBlock.coinbase.hash!!, 0)
        val utxoPool = UTXOPool()
        utxoPool.addUTXO(utxo, maxHeightBlock.coinbase.outputs[0])
        blocksToUTXOPoolsMap[ByteArrayWrapper(maxHeightBlock.hash!!)] = utxoPool
        blocks.add(maxHeightBlock)
    }

    /**
     * Add `block` to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at `height > (maxHeight - CUT_OFF_AGE)`.
     *
     *
     *
     *
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is `<=
     * CUT_OFF_AGE + 1`. As soon as `height > CUT_OFF_AGE + 1`, you cannot create a new block
     * at height 2.
     *
     * @return true if block is successfully added
     */
    fun addBlock(block: Block): Boolean {
        if (block.prevBlockHash == null) {
            return false
        }

        val parentNode = findParentNode(block)
        if (parentNode == null || parentNode.height < maxHeight - CUT_OFF_AGE) {
            return false
        }

        val parentPool = blocksToUTXOPoolsMap[ByteArrayWrapper(block.prevBlockHash)] ?: return false

        val txHandler = TxHandler(parentPool)
        val initialTxs = block.transactions.toTypedArray()
        val correctTxs = txHandler.handleTxs(initialTxs)
        if (initialTxs.size != correctTxs.size) {
            return false
        }

        val newPool = txHandler.utxoPool
        val newBlockArrayWrapper = ByteArrayWrapper(block.hash!!)
        blocksToUTXOPoolsMap[newBlockArrayWrapper] = newPool

        for (tx in correctTxs) {
            transactionPool.removeTransaction(tx.hash!!)
        }

        val utxo = UTXO(block.coinbase.hash!!, 0)
        newPool.addUTXO(utxo, block.coinbase.outputs[0])

        val newHeight = parentNode.height + 1
        if (newHeight > maxHeight) {
            maxHeight = newHeight
            maxHeightBlock = block
        }

        if (parentNode.getChildrenMap()[newBlockArrayWrapper] == null) {
            parentNode.getChildrenMap()[newBlockArrayWrapper] = Node(block, newHeight)
        }

        return true
    }

    fun addBlockAndNotify(block: Block) {
        blocks.add(block)
        setChanged()
        notifyObservers()
    }

    fun findParentNode(block: Block): Node? {
        val queue = LinkedList<Node>()
        val visited = hashSetOf<Node>()
        queue.add(head)
        visited.add(head)

        while (!queue.isEmpty()) {
            val node = queue.poll()
            if (node.isParent(block.prevBlockHash)) {
                return node
            }

            for (child in node.getChildrenMap().values) {
                if (!visited.contains(child)) {
                    visited.add(child)
                    queue.add(child)
                }
            }
        }
        return null
    }

    /**
     * Add a transaction to the transaction pool
     */
    fun addTransaction(tx: Transaction) {
        transactionPool.addTransaction(tx)
    }

    inner class Node(val block: Block, val height: Int) {

        private val childrenMap = linkedMapOf<ByteArrayWrapper, Node>()

        fun addChild(block: Block) {
            childrenMap[ByteArrayWrapper(block.hash!!)] = Node(block, height + 1)
        }

        fun getChildrenMap(): HashMap<ByteArrayWrapper, Node> {
            return childrenMap
        }

        fun isParent(blockHash: ByteArray?): Boolean {
            val current = ByteArrayWrapper(block.hash!!)
            return current == ByteArrayWrapper(blockHash!!)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false

            val node = other as Node?
            val current = ByteArrayWrapper(block.hash!!).hashCode()
            val toCompare = ByteArrayWrapper(node!!.block.hash!!).hashCode()

            return current == toCompare
        }

        override fun hashCode(): Int {
            return ByteArrayWrapper(block.hash!!).hashCode()
        }
    }

    companion object {

        const val CUT_OFF_AGE = 10
    }
}