package com.kaizendeveloper.bitcoinsandbox.db.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kaizendeveloper.bitcoinsandbox.transaction.ScriptSig
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionInput
import com.kaizendeveloper.bitcoinsandbox.util.wrap

@Entity(tableName = "mempool_inputs")
class TxInputEntity(
    var parentTxUUID: String,
    var prevTxHash: ByteArray,
    var outputIndex: Int,
    @Embedded
    var scriptSig: ScriptSig?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun toTransactionInput() = TransactionInput(prevTxHash.wrap(), outputIndex, scriptSig)
}