package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction

class Block(
    val hash: ByteArray,
    val prevBlockHash: ByteArray,
    val merkleRoot: ByteArray,
    val timestamp: Long,
    val target: Long,
    val nonce: Int,
    val transactions: List<Transaction>
)
