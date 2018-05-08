package com.kaizendeveloper.bitcoinsandbox.transaction

import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import java.io.OutputStream

class TransactionOutput(val amount: Double, val address: String) {

    fun serialize(outputStream: OutputStream) {
        with(outputStream) {
            write(amount.toByteArray())
            write(address.toByteArray())
        }
    }
}
