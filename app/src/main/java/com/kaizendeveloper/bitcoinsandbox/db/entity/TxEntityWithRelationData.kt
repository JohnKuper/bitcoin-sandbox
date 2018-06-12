package com.kaizendeveloper.bitcoinsandbox.db.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction


class TxEntityWithRelationData(
    @Embedded
    var txEntity: TxEntity
) {
    @Relation(parentColumn = "uuid", entityColumn = "parentTxUUID")
    var txInputs: List<TxInputEntity> = listOf()
    @Relation(parentColumn = "uuid", entityColumn = "parentTxUUID")
    var txOutputs: List<TxOutputEntity> = listOf()

    fun toTransaction(): Transaction {
        return Transaction().apply {
            inputs.addAll(txInputs.map { it.toTransactionInput() })
            outputs.addAll(txOutputs.map { it.toTransactionOutput() })
            hash = txEntity.hash
            isCoinbase = txEntity.isCoinbase
        }
    }
}
