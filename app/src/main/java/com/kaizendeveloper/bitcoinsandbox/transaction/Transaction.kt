package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.Crypto
import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.util.Arrays


class Transaction() {

    private var coinbase: Boolean = false

    val inputs: ArrayList<Input> = arrayListOf()
    val outputs: ArrayList<Output> = arrayListOf()
    var hash: ByteArray? = null

    constructor(amount: Double, address: PublicKey) : this() {
        coinbase = true
        addOutput(amount, address)
        computeTxHash()
    }

    fun addInput(prevTxHash: ByteArray, outputIndex: Int) {
        inputs.add(Input(prevTxHash, outputIndex))
    }

    fun addOutput(value: Double, address: PublicKey) {
        outputs.add(Output(value, address))
    }

    fun addSignature(signature: ByteArray, index: Int) {
        inputs[index].signature = signature
    }

    fun getRawDataToSign(index: Int): ByteArray {
        val sigData = arrayListOf<Byte>()
        sigData.addAll(inputs[index].serialize().asList())

        outputs.forEach {
            sigData.addAll(it.serialize().toList())
        }

        return sigData.toByteArray()
    }

    fun getRawTx(): ByteArray {
        val rawTx = arrayListOf<Byte>()
        inputs.forEach {
            rawTx.addAll(it.serialize().toList())
        }
        outputs.forEach {
            rawTx.addAll(it.serialize().toList())
        }

        return rawTx.toByteArray()
    }

    fun computeTxHash() {
        hash = Crypto.computeSha256(getRawTx())
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

        fun serialize(): ByteArray {
            val rawData = arrayListOf<Byte>()
            prevTxHash.forEach { rawData.add(it) }
            outputIndex.toByteArray().forEach { rawData.add(it) }
            signature?.forEach { rawData.add(it) }

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

    //TODO Change PublicKey to custom one and encapsulate logic for BitCoin public key inside it
    class Output(val amount: Double, val address: PublicKey) {

        fun serialize(): ByteArray {
            val rawData = arrayListOf<Byte>()
            amount.toByteArray().forEach { rawData.add(it) }
            with(address as ECPublicKey) {
                w.affineX.toByteArray().forEach { rawData.add(it) }
                w.affineY.toByteArray().forEach { rawData.add(it) }
            }

            return rawData.toByteArray()
        }
    }
}
