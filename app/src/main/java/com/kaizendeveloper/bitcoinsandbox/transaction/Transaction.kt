package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import com.kaizendeveloper.bitcoinsandbox.util.wrap
import java.io.ByteArrayOutputStream
import java.util.Arrays

class Transaction() {

    val inputs: ArrayList<TransactionInput> = arrayListOf()
    val outputs: ArrayList<TransactionOutput> = arrayListOf()

    var hash: ByteArray? = null

    var isCoinbase: Boolean = false
    var isConfirmed: Boolean = false

    constructor(amount: Double, address: String) : this() {
        isCoinbase = true
        addOutput(amount, address)
        build()
    }

    fun addInput(prevTxHash: ByteArray, outputIndex: Int) {
        inputs.add(TransactionInput(prevTxHash.wrap(), outputIndex))
    }

    fun addOutput(value: Double, address: String) {
        outputs.add(TransactionOutput(value, address))
    }

    fun addScriptSig(scriptSig: ScriptSig, inputIndex: Int) {
        inputs[inputIndex].scriptSig = scriptSig
    }

    fun getRawDataToSign(inputIndex: Int): ByteArray {
        return ByteArrayOutputStream().apply {
            inputs[inputIndex].serializeToSign(this)
            outputs.forEach { it.serialize(this) }
        }.toByteArray()
    }

    private fun getRawData(): ByteArray {
        return ByteArrayOutputStream().apply {
            inputs.forEach { it.serialize(this) }
            outputs.forEach { it.serialize(this) }
            write(System.currentTimeMillis().toByteArray())
        }.toByteArray()
    }

    fun build() {
        hash = Cipher.sha256(getRawData())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transaction

        if (inputs != other.inputs) return false
        if (outputs != other.outputs) return false
        if (!Arrays.equals(hash, other.hash)) return false
        if (isCoinbase != other.isCoinbase) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inputs.hashCode()
        result = 31 * result + outputs.hashCode()
        result = 31 * result + (hash?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + isCoinbase.hashCode()
        return result
    }
}
