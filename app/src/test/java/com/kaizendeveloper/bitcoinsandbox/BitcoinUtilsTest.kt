package com.kaizendeveloper.bitcoinsandbox

import com.kaizendeveloper.bitcoinsandbox.util.decodeBits
import com.kaizendeveloper.bitcoinsandbox.util.toHexString
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger

class BitcoinUtilsTest {

    @Test
    fun decodeCompactDifficulty() {
        assertEquals(BigInteger("2815ee000000000000000000000000000000000000000000", 16), decodeBits(0x182815ee))
        assertEquals(BigInteger("f1cc0000000000", 16), decodeBits(0x0800f1cc))

        println(
            BigInteger(decodeBits(0x207FFFFFL).toString()) / BigInteger(decodeBits(0x2007c0eFL).toString())
        )

        println(BigInteger(ByteArray(32) { 0xFF.toByte() }.toHexString(), 16))
        println(BigInteger(ByteArray(32) { 0xEF.toByte() }.toHexString(), 16))
    }
}
