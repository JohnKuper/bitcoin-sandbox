package com.kaizendeveloper.bitcoinsandbox.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.util.toUUIDString

@Entity(tableName = "blockchain")
class BlockEntity(
    var hash: ByteArray,
    var prevBlockHash: ByteArray,
    var merkleRoot: ByteArray,
    var timestamp: Long,
    var target: Long,
    var nonce: Int,
    /**
     * Serves as a foreign key.
     * Helps to connect [BlockEntity] with its relation data [TxWithRelationData] using [BlockWithTransactions] model.
     */
    @PrimaryKey
    var uuid: String = hash.toUUIDString()
) {

    companion object {

        fun fromBlock(block: Block) =
            BlockEntity(block.hash, block.prevBlockHash, block.merkleRoot, block.timestamp, block.target, block.nonce)
    }
}
