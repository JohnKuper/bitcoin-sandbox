package com.kaizendeveloper.bitcoinsandbox

import java.nio.ByteBuffer
import java.security.MessageDigest
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.util.Arrays


class Transaction() {

    val inputs: ArrayList<Input> = arrayListOf()
    val outputs: ArrayList<Output> = arrayListOf()
    private var coinbase: Boolean = false

    var hash: ByteArray? = null

    constructor(amount: Double, address: PublicKey) : this() {
        coinbase = true
        addOutput(amount, address)
        finalize()
    }

    fun addInput(prevTxHash: ByteArray, outputIndex: Int) {
        val input = Input(prevTxHash, outputIndex)
        inputs.add(input)
    }

    fun addOutput(value: Double, address: PublicKey) {
        val output = Output(value, address)
        outputs.add(output)
    }

    fun addSignature(signature: ByteArray, index: Int) {
        inputs[index].addSignature(signature)
    }

    //TODO Should be refactored because of the next method
    fun getRawDataToSign(index: Int): ByteArray? {
        val sigData = ArrayList<Byte>()
        if (index > inputs.size)
            return null
        val `in` = inputs[index]
        val prevTxHash = `in`.prevTxHash
        val b = ByteBuffer.allocate(Integer.SIZE / 8)
        b.putInt(`in`.outputIndex)
        val outputIndex = b.array()
        if (prevTxHash != null)
            for (i in prevTxHash.indices)
                sigData.add(prevTxHash[i])
        for (i in outputIndex.indices)
            sigData.add(outputIndex[i])
        for (op in outputs) {
            val bo = ByteBuffer.allocate(java.lang.Double.SIZE / 8)
            bo.putDouble(op.amount)
            val value = bo.array()
            val addressExponent = (op.address as RSAPublicKey).getPublicExponent().toByteArray()
            val addressModulus = (op.address as RSAPublicKey).getModulus().toByteArray()
            for (i in value.indices)
                sigData.add(value[i])
            for (i in addressExponent.indices)
                sigData.add(addressExponent[i])
            for (i in addressModulus.indices)
                sigData.add(addressModulus[i])
        }
        val sigD = ByteArray(sigData.size)
        var i = 0
        for (sb in sigData)
            sigD[i++] = sb
        return sigD
    }

    fun getRawTx(): ByteArray {
        val rawTx = ArrayList<Byte>()
        for (`in` in inputs) {
            val prevTxHash = `in`.prevTxHash
            val b = ByteBuffer.allocate(Integer.SIZE / 8)
            b.putInt(`in`.outputIndex)
            val outputIndex = b.array()
            val signature = `in`.signature
            if (prevTxHash != null)
                for (i in prevTxHash.indices)
                    rawTx.add(prevTxHash[i])
            for (i in outputIndex.indices)
                rawTx.add(outputIndex[i])
            if (signature != null)
                for (i in signature.indices)
                    rawTx.add(signature[i])
        }
        for (op in outputs) {
            val b = ByteBuffer.allocate(java.lang.Double.SIZE / 8)
            b.putDouble(op.amount)
            val value = b.array()
            val addressExponent = (op.address as RSAPublicKey).publicExponent.toByteArray()
            val addressModulus = op.address.modulus.toByteArray()
            for (i in value.indices)
                rawTx.add(value[i])
            for (i in addressExponent.indices)
                rawTx.add(addressExponent[i])
            for (i in addressModulus.indices)
                rawTx.add(addressModulus[i])
        }
        val tx = ByteArray(rawTx.size)
        var i = 0
        for (b in rawTx)
            tx[i++] = b
        return tx
    }

    fun finalize() {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(getRawTx())
        hash = md.digest()
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

    class Input(prevHash: ByteArray? = null, val outputIndex: Int) {

        val prevTxHash: ByteArray? = if (prevHash == null)
            null
        else
            Arrays.copyOf(prevHash, prevHash.size)

        var signature: ByteArray? = null

        fun addSignature(sig: ByteArray?) {
            signature = if (sig == null)
                null
            else
                Arrays.copyOf(sig, sig.size)
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
            result = 31 * result + (prevTxHash?.let { Arrays.hashCode(it) } ?: 0)
            result = 31 * result + (signature?.let { Arrays.hashCode(it) } ?: 0)
            return result
        }
    }

    data class Output(val amount: Double, val address: PublicKey)
}
