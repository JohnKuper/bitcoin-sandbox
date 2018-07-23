package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.db.repository.BlockchainRepository
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionHandler
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class BlockHandler @Inject constructor(
    private val txHandler: TransactionHandler,
    private val blockchainRepo: BlockchainRepository
) {

    fun handleBlock(block: Block): Completable {
        return Single.just(block)
            .filter { it.prevBlockHash.contentEquals(blockchainRepo.getLastHash().blockingGet()) }
            .flatMapCompletable {
                txHandler
                    .handle(it.transactions.single { it.isCoinbase })
                    .andThen(blockchainRepo.insert(it))
            }
    }
}