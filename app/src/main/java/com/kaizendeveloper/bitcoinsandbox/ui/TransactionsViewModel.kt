package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.blockchain.Miner
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.db.repository.MempoolRepository
import com.kaizendeveloper.bitcoinsandbox.transaction.ProgressStatus
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.transaction.TransferManager
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class TransactionsViewModel @Inject constructor(
    private val transferManager: TransferManager,
    private val miner: Miner,
    private val mempoolRepository: MempoolRepository
) : ViewModel() {

    val transactions: LiveData<List<Transaction>> = mempoolRepository.transactions
    val blocks: LiveData<List<Block>> = mempoolRepository.blocks

    val transactionStatus: BehaviorSubject<ProgressStatus> = BehaviorSubject.create()

    fun sendCoins(transferAmount: Double, user: User, recipient: User): Completable {
        return transferManager
            .sendCoins(transferAmount, user, recipient)
            .doOnSubscribe { transactionStatus.onNext(ProgressStatus.IN_PROGRESS) }
            .doFinally { transactionStatus.onNext(ProgressStatus.COMPLETED) }
    }

    fun mine(user: User): Single<Block> {
        return miner.mine(user)
            .doOnSubscribe { transactionStatus.onNext(ProgressStatus.IN_PROGRESS) }
            .doFinally { transactionStatus.onNext(ProgressStatus.COMPLETED) }
            .doOnSuccess { mempoolRepository.insert(it) }
    }
}
