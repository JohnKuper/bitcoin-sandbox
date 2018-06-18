package com.kaizendeveloper.bitcoinsandbox.db.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block

class BlockWithTransactions(
    @Embedded
    var blockEntity: BlockEntity
) {
    @Relation(parentColumn = "uuid", entityColumn = "parentBlockId", entity = TxEntity::class)
    var transactions: List<TxWithRelationData> = listOf()

    fun toBlock(): Block {
        return Block(
            blockEntity.hash,
            blockEntity.prevBlockHash,
            blockEntity.merkleRoot,
            blockEntity.timestamp,
            blockEntity.target,
            blockEntity.nonce,
            transactions.map { it.toTransaction() }
        )
    }
}
