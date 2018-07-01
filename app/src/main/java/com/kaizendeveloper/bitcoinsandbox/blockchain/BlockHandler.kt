package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.db.repository.BlockchainRepository
import com.kaizendeveloper.bitcoinsandbox.transaction.TxHandler
import io.reactivex.Completable
import javax.inject.Inject

class BlockHandler @Inject constructor(
    private val txHandler: TxHandler,
    private val blockchainRepo: BlockchainRepository
) {

    fun handleBlock(block: Block): Completable {
        val coinbaseTx = arrayOf(block.transactions.single { it.isCoinbase })
        return txHandler
            .handleTxs(coinbaseTx)
            .andThen(blockchainRepo.insert(block))
    }
}