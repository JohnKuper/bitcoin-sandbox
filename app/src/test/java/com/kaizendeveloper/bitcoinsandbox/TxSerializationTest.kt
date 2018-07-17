package com.kaizendeveloper.bitcoinsandbox

import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput
import com.kaizendeveloper.bitcoinsandbox.util.toByteArray
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.io.ByteArrayOutputStream

class TxSerializationTest {

    @Test
    fun shouldBeSerializedToStream() {
        val amount = 15.00
        val address = "1KDKkfHmVqyVBZ2iNZr6Fkqwv8ZkTS9nt7"
        val txOutput = TransactionOutput(amount, address)

        val actual = ByteArrayOutputStream().apply {
            txOutput.serialize(this)
        }.toByteArray()

        val expected = ByteArrayOutputStream().apply {
            write(amount.toByteArray())
            write(address.toByteArray())
        }.toByteArray()

        assertArrayEquals(expected, actual)
    }
}
