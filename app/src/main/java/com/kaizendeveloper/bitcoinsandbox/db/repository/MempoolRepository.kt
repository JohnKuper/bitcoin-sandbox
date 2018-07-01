package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import com.kaizendeveloper.bitcoinsandbox.db.dao.MempoolDao
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxInputEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxOutputEntity
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.toUUIDString
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

//TODO Make All repos testable by passing executors to them separately, otherwise doAsync is not testable
@Singleton
class MempoolRepository @Inject constructor(
    private val db: SandboxDatabase,
    private val mempoolDao: MempoolDao
) {

    val transactions: LiveData<List<Transaction>> =
        Transformations.switchMap(mempoolDao.getAll()) { dbTransactions ->
            MutableLiveData<List<Transaction>>().apply {
                value = dbTransactions.map { it.toTransaction() }
            }
        }

    fun insert(transaction: Transaction): Completable {
        return Completable.fromAction {
            val txHash = transaction.hash!!
            val txUUID = txHash.toUUIDString()

            val txEntity = TxEntity.fromTransaction(transaction)
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
        }.subscribeOn(Schedulers.io())
    }

    fun getAllUnconfirmed(): Single<List<Transaction>> = mempoolDao.getAllUnconfirmed().flatMap { dbTransactions ->
        Single.just(dbTransactions.map { it.toTransaction() })
    }
}
