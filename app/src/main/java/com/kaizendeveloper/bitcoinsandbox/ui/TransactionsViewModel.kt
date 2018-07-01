package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.blockchain.Miner
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.db.repository.BlockchainRepository
import com.kaizendeveloper.bitcoinsandbox.db.repository.MempoolRepository
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.transaction.TransferManager
import com.kaizendeveloper.bitcoinsandbox.util.requireValue
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class TransactionsViewModel @Inject constructor(
    private val transferManager: TransferManager,
    private val miner: Miner,
    private val blockchainRepository: BlockchainRepository,
    mempoolRepository: MempoolRepository
) : ViewModel() {

    val transactions: LiveData<List<Transaction>> = mempoolRepository.transactions
    val blocks: LiveData<List<Block>> = blockchainRepository.blocks

    val operationInProgress: LiveData<Boolean> = MutableLiveData<Boolean>()

    fun sendCoins(transferAmount: Double, user: User, recipient: User): Completable {
        return transferManager
            .sendCoins(transferAmount, user, recipient)
            .doOnSubscribe { notifyOperationInProgress(true) }
            .doFinally { notifyOperationInProgress(false) }
    }

    fun mine(user: User): Single<Block> {
        return miner.mine(user)
            .doOnSuccess { blockchainRepository.insert(it) }
            .doOnSubscribe { notifyOperationInProgress(true) }
            .doFinally { notifyOperationInProgress(false) }
    }

    //TODO Don't rely on live data for business logic
    fun getTransactionByHash(hash: ByteArray): Transaction {
        return transactions.requireValue().single {
            it.hash!!.contentEquals(hash)
        }
    }

    private fun notifyOperationInProgress(inProgress: Boolean) {
        (operationInProgress as MutableLiveData).postValue(inProgress)
    }
}
