package com.kaizendeveloper.bitcoinsandbox.db.repository

import com.kaizendeveloper.bitcoinsandbox.db.dao.UTXOPoolDao
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class UTXOPoolRepository @Inject constructor(
    private val utxoPoolDao: UTXOPoolDao
) {

    val utxoPool = utxoPoolDao.getAll()

    fun insert(utxo: UTXO, txOutput: TransactionOutput) {
        doAsync { utxoPoolDao.insert(UTXOWithTxOutput(utxo, txOutput)) }
    }

    fun delete(utxo: UTXO) {
        doAsync { utxoPoolDao.delete(utxo.txHash.data, utxo.outputIndex) }
    }
}
