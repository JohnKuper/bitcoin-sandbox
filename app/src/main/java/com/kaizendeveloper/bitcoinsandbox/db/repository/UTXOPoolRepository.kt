package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.app.Application
import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO
import org.jetbrains.anko.doAsync

//TODO Remove db from repository
class UTXOPoolRepository(app: Application) {

    private val db = SandboxDatabase.getInstance(app)
    private val utxoPoolDao = db.utxoPoolDao()

    val utxoPool = utxoPoolDao.getAll()

    fun insert(utxo: UTXO, txOutput: TransactionOutput) {
        doAsync { utxoPoolDao.insert(UTXOWithTxOutput(utxo, txOutput)) }
    }

    fun delete(utxo: UTXO) {
        doAsync { utxoPoolDao.delete(utxo.txHash, utxo.outputIndex) }
    }
}
