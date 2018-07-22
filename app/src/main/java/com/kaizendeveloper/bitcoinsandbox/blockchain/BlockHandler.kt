package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.db.repository.BlockchainRepository
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionHandler
import io.reactivex.Completable
import javax.inject.Inject

class BlockHandler @Inject constructor(
    private val txHandler: TransactionHandler,
    private val blockchainRepo: BlockchainRepository
) {

    fun handleBlock(block: Block): Completable {
        return blockchainRepo
            .getLastHash()
            .filter { it.contentEquals(block.prevBlockHash) }
            .flatMapCompletable {
                txHandler
                    .handle(block.transactions.single { it.isCoinbase })
                    .andThen(blockchainRepo.insert(block))
            }
    }
}