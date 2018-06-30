package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import com.kaizendeveloper.bitcoinsandbox.db.dao.MempoolDao
import com.kaizendeveloper.bitcoinsandbox.db.entity.BlockEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxInputEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxOutputEntity
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.toUUIDString
import io.reactivex.Single
import javax.inject.Inject

//TODO Make All repos testable by passing executors to them separately, otherwise doAsync is not testable
class MempoolRepository @Inject constructor(
    private val db: SandboxDatabase,
    private val mempoolDao: MempoolDao
) {

    val transactions: LiveData<List<Transaction>> =
        Transformations.switchMap(mempoolDao.getAllTransactions()) { dbTransactions ->
            MutableLiveData<List<Transaction>>().apply {
                value = dbTransactions.map { it.toTransaction() }
            }
        }

    val blocks: LiveData<List<Block>> = Transformations.switchMap(mempoolDao.getAllBlocks()) { dbBlocks ->
        MutableLiveData<List<Block>>().apply {
            value = dbBlocks.map { it.toBlock() }
        }
    }

    fun insert(transaction: Transaction) {
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
    }

    fun insert(block: Block) {
        val blockEntity = BlockEntity.fromBlock(block)

        db.runInTransaction {
            mempoolDao.insert(blockEntity)
            block.transactions.forEach {
                it.isConfirmed = true
                mempoolDao.update(TxEntity.fromTransaction(it).apply {
                    parentBlockId = blockEntity.uuid
                })
            }
        }
    }

    fun getAllUnconfirmed(): Single<List<Transaction>> = mempoolDao.getAllUnconfirmed().flatMap { dbTransactions ->
        Single.just(dbTransactions.map { it.toTransaction() })
    }
}
