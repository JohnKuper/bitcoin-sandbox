package com.kaizendeveloper.bitcoinsandbox.db.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO

@Entity(tableName = "utxo_pool")
data class UTXOWithTxOutput(
    @Embedded
    var utxo: UTXO,
    @Embedded
    var txOutput: TransactionOutput
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
