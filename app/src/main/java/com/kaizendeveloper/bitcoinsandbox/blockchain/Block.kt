package com.kaizendeveloper.bitcoinsandbox.blockchain

const val CURRENT_TARGET = 0x200000FFL

class Block(
    val hash: ByteArray,
    val prevBlockHash: ByteArray,
    val merkleRoot: ByteArray,
    val timestamp: Long,
    val nonce: Int
) {

}
