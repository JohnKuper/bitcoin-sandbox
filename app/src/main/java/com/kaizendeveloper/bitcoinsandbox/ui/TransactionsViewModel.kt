package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.db.repository.MempoolRepository
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import javax.inject.Inject

class TransactionsViewModel @Inject constructor(
    mempoolRepository: MempoolRepository
) : ViewModel() {

    val transactions: LiveData<List<Transaction>> = mempoolRepository.transactions
    val blocks: LiveData<List<Block>> = mempoolRepository.blocks
}
