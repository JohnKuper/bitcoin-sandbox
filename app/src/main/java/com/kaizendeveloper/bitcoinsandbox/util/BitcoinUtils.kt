package com.kaizendeveloper.bitcoinsandbox.util

import java.math.BigInteger

fun decodeBits(compact: Long): BigInteger {
    val size = (compact shr 24).toInt() and 0xFF
    val bytes = ByteArray(size)
    if (size >= 1) bytes[0] = (compact shr 16 and 0xFF).toByte()
    if (size >= 2) bytes[1] = (compact shr 8 and 0xFF).toByte()
    if (size >= 3) bytes[2] = (compact and 0xFF).toByte()
    return BigInteger(bytes)
}
