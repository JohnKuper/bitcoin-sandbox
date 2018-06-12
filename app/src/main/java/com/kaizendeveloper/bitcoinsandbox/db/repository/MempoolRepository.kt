package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import com.kaizendeveloper.bitcoinsandbox.db.dao.MempoolDao
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxInputEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxOutputEntity
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import org.jetbrains.anko.doAsync
import java.util.UUID

//TODO Make All repos testable by passing executors to them separately, otherwise doAsync is not testable
class MempoolRepository(
    context: Context,
    private val db: SandboxDatabase = SandboxDatabase.getInstance(context)
) {

    private val mempoolDao: MempoolDao = db.mempoolDao()

    val transactions: LiveData<List<Transaction>>

    init {
        transactions = Transformations.switchMap(mempoolDao.getAll()) { dbTransactions ->
            MutableLiveData<List<Transaction>>().apply {
                value = dbTransactions.map { it.toTransaction() }
            }
        }
    }

    fun insert(transaction: Transaction) {
        doAsync {
            val txHash = transaction.hash!!
            val txUUID = UUID.nameUUIDFromBytes(txHash).toString()

            val txEntity = TxEntity(txUUID, txHash, transaction.isCoinbase)
            val txInputs = transaction.inputs.map {
                TxInputEntity(txUUID, it.txHash.data, it.outputIndex, it.scriptSig)
            }
            val txOutputs = transaction.outputs.map {
                TxOutputEntity(txUUID, it.amount, it.address)
            }

            db.runInTransaction {
                mempoolDao.insert(txEntity)
                txInputs.forEach { mempoolDao.insert(it) }
                txOutputs.forEach { mempoolDao.insert(it) }
            }
        }
    }
}
