package com.kaizendeveloper.bitcoinsandbox.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput

@Entity(tableName = "mempool_outputs")
class TxOutputEntity(
    /**
     * Serves as a foreign key for [TxEntity.uuid] to connect [TxOutputEntity] with its parent.
     */
    var parentTxUUID: String,
    var amount: Double,
    var address: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun toTransactionOutput() = TransactionOutput(amount, address)
}
