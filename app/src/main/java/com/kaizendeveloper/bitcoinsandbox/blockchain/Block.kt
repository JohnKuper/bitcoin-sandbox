package com.kaizendeveloper.bitcoinsandbox.blockchain

class Block(
    val hash: ByteArray,
    val prevBlockHash: ByteArray,
    val merkleRoot: ByteArray,
    val timestamp: Long,
    val target: Long,
    val nonce: Int
)
