package com.kaizendeveloper.bitcoinsandbox.model

import com.kaizendeveloper.bitcoinsandbox.util.Base58
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.toHex
import org.bouncycastle.jce.interfaces.ECPublicKey

class BitCoinPublicKey(val publicKey: ECPublicKey) {

    val address = toAddress()
    val encoded = encodedWith04Prefix()

    private fun toAddress(): String {
        println("public key: x=${publicKey.q.affineXCoord}, y=${publicKey.q.affineYCoord}")

        val pkWithPrefix = encodedWith04Prefix()
        println("public key with prefix (HEX): $pkWithPrefix")

        val pkSha256 = Cipher.sha256(pkWithPrefix.toByteArray())
        println("public key SHA-256 (HEX): ${pkSha256.toHex()}")

        val rmd160 = Cipher.ripeMD160(pkSha256)
        println("public key RIPEMD160 (HEX): ${rmd160.toHex()}")

        val rmd160WithVersion = ByteArray(rmd160.size + 1).apply { this[0] = 0x00 }
        System.arraycopy(rmd160, 0, rmd160WithVersion, 1, rmd160.size)
        println("public key RIPEMD160 with version prefix (HEX) ${rmd160WithVersion.toHex()}")

        val rmdWithVersionShaTwice = Cipher.sha256Twice(rmd160WithVersion)
        val rmdWithCheckSum = ByteArray(rmd160WithVersion.size + BASE58_CHECKSUM_SIZE)
        System.arraycopy(rmd160WithVersion, 0, rmdWithCheckSum, 0, rmd160WithVersion.size)
        System.arraycopy(rmdWithVersionShaTwice, 0, rmdWithCheckSum, rmd160WithVersion.size, BASE58_CHECKSUM_SIZE)
        println("rmdWithCheckSum: ${rmdWithCheckSum.toHex()}")

        val address = Base58.encode(rmdWithCheckSum)
        println("BitCoin address: $address")

        return address
    }

    private fun encodedWith04Prefix(): String {
        return with(publicKey) {
            val stringX = q.affineXCoord.toBigInteger().toString(16)
            val stringY = q.affineYCoord.toBigInteger().toString(16)
            "$UNCOMPRESSED_PUBLIC_KEY_PREFIX$stringX$stringY".toUpperCase()
        }
    }

    companion object {
        private const val UNCOMPRESSED_PUBLIC_KEY_PREFIX = "04"
        private const val BASE58_CHECKSUM_SIZE = 4
    }
}
