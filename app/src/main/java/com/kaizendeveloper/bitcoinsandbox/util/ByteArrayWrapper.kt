package com.kaizendeveloper.bitcoinsandbox.util

import java.util.Arrays

/**
 * Wrapper for [ByteArray] to reduce boilerplate while using equals/hashcode.
 */
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
