package com.kaizendeveloper.bitcoinsandbox.util

import java.util.UUID

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun ByteArray.toHexString(): String {
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}

fun ByteArray.wrap(): ByteArrayWrapper = ByteArrayWrapper(this)

fun ByteArray.toUUIDString(): String = UUID.nameUUIDFromBytes(this).toString()

fun randomByteArray() = UUID.randomUUID().toString().toByteArray()
