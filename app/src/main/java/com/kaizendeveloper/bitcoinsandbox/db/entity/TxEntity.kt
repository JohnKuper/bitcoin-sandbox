package com.kaizendeveloper.bitcoinsandbox.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "mempool_transactions")
class TxEntity(
    var uuid: String,
    var hash: ByteArray,
    var isCoinbase: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}