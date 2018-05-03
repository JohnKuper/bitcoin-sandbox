package com.kaizendeveloper.bitcoinsandbox.util

import java.util.Arrays


class ByteArrayWrapper(b: ByteArray) {

    private val contents: ByteArray?

    init {
        contents = ByteArray(b.size)
        System.arraycopy(b, 0, contents, 0, contents.size)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (javaClass != other.javaClass) {
            return false
        }

        val otherB = other as ByteArrayWrapper?
        val b = otherB!!.contents
        if (contents == null) {
            return b == null
        } else {
            if (b == null)
                return false
            else {
                if (contents.size != b.size)
                    return false
                for (i in b.indices)
                    if (contents[i] != b[i])
                        return false
                return true
            }
        }
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(contents)
    }
}
