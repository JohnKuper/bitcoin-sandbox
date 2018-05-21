package com.kaizendeveloper.bitcoinsandbox.blockchain

import com.kaizendeveloper.bitcoinsandbox.util.Cipher


class MerkleRootGenerator {

    companion object {

        fun generate(hashes: List<ByteArray>): ByteArray {
            return generateInner(padWithZeros(hashes))
        }

        private fun generateInner(hashes: List<ByteArray>): ByteArray {
            val size = hashes.size
            return if (size == 1) {
                hashes[0]
            } else {
                println("Level size = $size")
                val levelHashes = arrayListOf<ByteArray>()
                for (i in 0 until size step 2) {
                    val combinedHashes = hashes[i] + hashes[i + 1]
                    levelHashes.add(Cipher.sha256(combinedHashes))
                }
                generateInner(levelHashes)
            }
        }

        private fun padWithZeros(hashes: List<ByteArray>): List<ByteArray> {
            var size = hashes.size
            if (isPowOf2(size)) {
                return hashes
            }

            val paddedHashes = ArrayList(hashes)
            while (!isPowOf2(size++)) {
                paddedHashes.add(Cipher.zeroHash)
            }

            return paddedHashes
        }

        private fun isPowOf2(num: Int) = num and (num - 1) == 0
    }
}
