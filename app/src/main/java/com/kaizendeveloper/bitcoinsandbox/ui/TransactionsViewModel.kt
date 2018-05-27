package com.kaizendeveloper.bitcoinsandbox.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.kaizendeveloper.bitcoinsandbox.transaction.Mempool
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction

//TODO Should be aware about mempool table in DB. Right now it's work directly with Mempool singleton, which is wrong
class TransactionsViewModel(app: Application) : AndroidViewModel(app) {

    val observableTransactions: LiveData<List<Transaction>> = Mempool.observableTransactions
}