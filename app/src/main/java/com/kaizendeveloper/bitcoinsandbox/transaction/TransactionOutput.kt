package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import java.io.OutputStream

/**
 * Transaction output that can be spent later by the owner whose address is [address]
 */
data class TransactionOutput(val amount: Double, val address: String) : Comparable<TransactionOutput> {

    fun serialize(outputStream: OutputStream) {
        with(outputStream) {
            write(amount.toByteArray())
            write(address.toByteArray())
        }
    }

    override fun compareTo(other: TransactionOutput) = amount.compareTo(other.amount)
}
