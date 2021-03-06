package com.kaizendeveloper.bitcoinsandbox.model

import com.kaizendeveloper.bitcoinsandbox.util.Base58
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.toHexString
import java.security.interfaces.ECPublicKey

/**
 * Generates bitcoin address according to rules:
 * (https://github.com/bitcoinbook/bitcoinbook/blob/develop/ch04.asciidoc#pubkey_to_address)
 */
class AddressGenerator {

    companion object {

        private const val UNCOMPRESSED_PUBLIC_KEY_PREFIX = "04"
        private const val BASE58_CHECKSUM_SIZE = 4

        fun generate(publicKey: ECPublicKey): String {
            println("public key: x=${publicKey.w.affineX}, y=${publicKey.w.affineY}")

            val pkWithPrefix = encodedWith04Prefix(publicKey)
            println("public key with prefix (HEX): $pkWithPrefix")

            val pkSha256 = Cipher.sha256(pkWithPrefix.toByteArray())
            println("public key SHA-256 (HEX): ${pkSha256.toHexString()}")

            val rmd160 = Cipher.ripeMD160(pkSha256)
            println("public key RIPEMD160 (HEX): ${rmd160.toHexString()}")

            val rmd160WithVersion = ByteArray(rmd160.size + 1).apply { this[0] = 0x00 }
            System.arraycopy(rmd160, 0, rmd160WithVersion, 1, rmd160.size)
            println("public key RIPEMD160 with version prefix (HEX) ${rmd160WithVersion.toHexString()}")

            val rmdWithVersionShaTwice = Cipher.sha256Twice(rmd160WithVersion)
            val rmdWithCheckSum = ByteArray(rmd160WithVersion.size + BASE58_CHECKSUM_SIZE)
            System.arraycopy(rmd160WithVersion, 0, rmdWithCheckSum, 0, rmd160WithVersion.size)
            System.arraycopy(rmdWithVersionShaTwice, 0, rmdWithCheckSum, rmd160WithVersion.size, BASE58_CHECKSUM_SIZE)
            println("rmdWithCheckSum: ${rmdWithCheckSum.toHexString()}")

            val address = Base58.encode(rmdWithCheckSum)
            println("BitCoin address: $address")

            return address
        }

        private fun encodedWith04Prefix(publicKey: ECPublicKey): String {
            return with(publicKey) {
                val stringX = w.affineX.toString(16)
                val stringY = w.affineY.toString(16)
                "$UNCOMPRESSED_PUBLIC_KEY_PREFIX$stringX$stringY".toUpperCase()
            }
        }
    }
}