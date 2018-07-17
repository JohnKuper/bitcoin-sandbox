package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import com.kaizendeveloper.bitcoinsandbox.db.dao.UTXOPoolDao
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO
import com.kaizendeveloper.bitcoinsandbox.util.requireValue
import com.kaizendeveloper.bitcoinsandbox.util.wrap
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UTXOPoolRepository @Inject constructor(
    private val db: SandboxDatabase,
    private val utxoPoolDao: UTXOPoolDao
) {

    val utxoPool: LiveData<HashMap<UTXO, TransactionOutput>> =
        Transformations.switchMap(utxoPoolDao.getAll()) {
            MutableLiveData<HashMap<UTXO, TransactionOutput>>().apply {
                value = it.associateTo(hashMapOf()) { it.utxo to it.txOutput }
            }
        }

    fun updatePool(transaction: Transaction): Completable {
        return Completable.fromAction {
            db.runInTransaction {
                transaction.inputs.forEach {
                    val oldUtxo = UTXO.fromTxInput(it)
                    utxoPoolDao.delete(oldUtxo.txHash.data, oldUtxo.outputIndex)
                }
                transaction.outputs.forEachIndexed { index, output ->
                    val newUtxo = UTXO(transaction.hash!!.wrap(), index)
                    utxoPoolDao.insert(UTXOWithTxOutput(newUtxo, output))
                }
            }
        }.subscribeOn(Schedulers.io())
    }

    fun getUtxoPool(): HashMap<UTXO, TransactionOutput> {
        return utxoPoolDao.getAll().requireValue().associateTo(hashMapOf()) { it.utxo to it.txOutput }
    }
}
