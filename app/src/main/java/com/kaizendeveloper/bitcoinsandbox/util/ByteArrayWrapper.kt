package com.kaizendeveloper.bitcoinsandbox.util

import java.util.Arrays

//TODO Replace all ByteArrays properties that are necessary for equals logic on this class
class ByteArrayWrapper(byteArray: ByteArray) {

    val data: ByteArray = byteArray.copyOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteArrayWrapper

        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(data)
    }
}
