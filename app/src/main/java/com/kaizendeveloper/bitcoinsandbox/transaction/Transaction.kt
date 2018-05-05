package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.model.BitCoinPublicKey
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import java.util.Arrays


class Transaction() {

    var coinbase: Boolean = false

    val inputs: ArrayList<Input> = arrayListOf()
    val outputs: ArrayList<Output> = arrayListOf()
    var hash: ByteArray? = null

    constructor(amount: Double, publicKey: BitCoinPublicKey) : this() {
        coinbase = true
        addOutput(amount, publicKey)
        build()
    }

    fun addInput(prevTxHash: ByteArray, outputIndex: Int) {
        inputs.add(Input(prevTxHash, outputIndex))
    }

    fun addOutput(value: Double, publicKey: BitCoinPublicKey) {
        outputs.add(Output(value, publicKey))
    }

    fun addSignature(signature: ByteArray, index: Int) {
        inputs[index].signature = signature
    }

    fun getRawDataToSign(index: Int): ByteArray {
        val sigData = arrayListOf<Byte>()
        sigData.addAll(inputs[index].toByteArrayForSign().toList())
        outputs.forEach {
            sigData.addAll(it.toByteArray().toList())
        }

        return sigData.toByteArray()
    }

    fun getRawTx(): ByteArray {
        val rawTx = arrayListOf<Byte>()
        inputs.forEach {
            rawTx.addAll(it.toByteArray().toList())
        }
        outputs.forEach {
            rawTx.addAll(it.toByteArray().toList())
        }

        return rawTx.toByteArray()
    }

    fun build() {
        hash = Cipher.sha256(getRawTx())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transaction

        if (!Arrays.equals(hash, other.hash)) return false
        if (inputs != other.inputs) return false
        if (outputs != other.outputs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hash?.let { Arrays.hashCode(it) } ?: 0
        result = 31 * result + inputs.hashCode()
        result = 31 * result + outputs.hashCode()
        result = 31 * result + coinbase.hashCode()
        return result
    }

    class Input(prevHash: ByteArray, val outputIndex: Int) {

        val prevTxHash: ByteArray = Arrays.copyOf(prevHash, prevHash.size)
        var signature: ByteArray? = null
            set(value) {
                if (value != null) {
                    field = Arrays.copyOf(value, value.size)
                }
            }

        fun toByteArray(): ByteArray {
            val rawData = arrayListOf<Byte>().apply {
                addAll(toByteArrayForSign().toList())
            }
            signature?.forEach { rawData.add(it) }

            return rawData.toByteArray()
        }

        fun toByteArrayForSign(): ByteArray {
            val rawData = arrayListOf<Byte>()
            prevTxHash.forEach { rawData.add(it) }
            outputIndex.toByteArray().forEach { rawData.add(it) }

            return rawData.toByteArray()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Input

            if (outputIndex != other.outputIndex) return false
            if (!Arrays.equals(prevTxHash, other.prevTxHash)) return false
            if (!Arrays.equals(signature, other.signature)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = outputIndex
            result = 31 * result + prevTxHash.let { Arrays.hashCode(it) }
            result = 31 * result + (signature?.let { Arrays.hashCode(it) } ?: 0)

            return result
        }
    }

    class Output(val amount: Double, val bitCoinPublicKey: BitCoinPublicKey) {

        fun toByteArray(): ByteArray {
            val rawData = arrayListOf<Byte>()
            amount.toByteArray().forEach { rawData.add(it) }
            bitCoinPublicKey.address.toByteArray().forEach { rawData.add(it) }

            return rawData.toByteArray()
        }
    }
}
