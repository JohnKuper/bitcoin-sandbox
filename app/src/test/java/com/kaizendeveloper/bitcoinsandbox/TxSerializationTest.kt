package com.kaizendeveloper.bitcoinsandbox

import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput
import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.Arrays


class TxSerializationTest {

    @Test
    fun shouldBeSerializedToStream() {
        val amount = 15.00
        val address = "some_address"
        val txOutput = TransactionOutput(amount, address)

        val actual = ByteArrayOutputStream().apply {
            txOutput.serialize(this)
        }.toByteArray()

        val expected = ByteArrayOutputStream().apply {
            write(amount.toByteArray())
            write(address.toByteArray())
        }.toByteArray()

        assertTrue(Arrays.equals(actual, expected))
    }
}
