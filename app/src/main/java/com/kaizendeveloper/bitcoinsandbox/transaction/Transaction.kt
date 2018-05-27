package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import java.io.ByteArrayOutputStream
import java.util.UUID

class Transaction() {

    val inputs: ArrayList<TransactionInput> = arrayListOf()
    val outputs: ArrayList<TransactionOutput> = arrayListOf()

    var hash: ByteArray? = null

    var isCoinbase: Boolean = false

    constructor(amount: Double, address: String) : this() {
        isCoinbase = true
        addOutput(amount, address)
        build()
    }

    fun addInput(prevTxHash: ByteArray, outputIndex: Int) {
        inputs.add(TransactionInput(prevTxHash, outputIndex))
    }

    fun addOutput(value: Double, address: String) {
        outputs.add(TransactionOutput(value, address))
    }

    fun addScriptSig(scriptSig: TransactionInput.ScriptSig, inputIndex: Int) {
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
            write(UUID.randomUUID().toString().toByteArray())
        }.toByteArray()
    }

    fun build() {
        hash = Cipher.sha256(getRawData())
    }
}
