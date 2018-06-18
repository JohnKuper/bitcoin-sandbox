package com.kaizendeveloper.bitcoinsandbox.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.toUUIDString

@Entity(tableName = "mempool_transactions")
class TxEntity(
    var hash: ByteArray,
    var isCoinbase: Boolean = false,
    var isConfirmed: Boolean = false,
    var parentBlockId: String? = null,
    @PrimaryKey
    var uuid: String = hash.toUUIDString()
) {

    companion object {

        fun fromTransaction(transaction: Transaction): TxEntity {
            return TxEntity(transaction.hash!!, transaction.isCoinbase, transaction.isConfirmed)
        }
    }
}
