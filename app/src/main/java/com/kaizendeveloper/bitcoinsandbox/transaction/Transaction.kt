package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.model.BitCoinPublicKey
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import java.io.ByteArrayOutputStream
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
        return ByteArrayOutputStream().apply {
            write(inputs[index].toByteArray())
            outputs.forEach {
                write(it.toByteArray())
            }
        }.toByteArray()
    }

    fun getRawTx(): ByteArray {
        return ByteArrayOutputStream().apply {
            inputs.forEach {
                write(it.toByteArray())
            }
            outputs.forEach {
                write(it.toByteArray())
            }
        }.toByteArray()
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
            return ByteArrayOutputStream().apply {
                write(prevTxHash)
                write(outputIndex.toByteArray())
                //TODO Fix signature serializing
//                write(signature)
            }.toByteArray()
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
            return ByteArrayOutputStream().apply {
                write(amount.toByteArray())
                write(bitCoinPublicKey.address.toByteArray())
            }.toByteArray()
        }
    }
}
