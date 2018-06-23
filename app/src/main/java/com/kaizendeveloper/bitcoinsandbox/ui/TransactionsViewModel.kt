package com.kaizendeveloper.bitcoinsandbox.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.kaizendeveloper.bitcoinsandbox.SandboxApplication
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.db.repository.MempoolRepository
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction

class TransactionsViewModel(app: Application) : AndroidViewModel(app) {

    //TODO Use correct injectioin
    private val mempoolRepository: MempoolRepository = SandboxApplication.application.mempoolRepo

    val transactions: LiveData<List<Transaction>> = mempoolRepository.transactions
    val blocks: LiveData<List<Block>> = mempoolRepository.blocks
}
