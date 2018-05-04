package com.kaizendeveloper.bitcoinsandbox

import com.kaizendeveloper.bitcoinsandbox.model.IdentityFactory
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.toHex
import org.junit.Test
import java.security.interfaces.ECPublicKey


class TransactionTest {

    @Test
    fun testRawDataToSign() {
        val keyPair = IdentityFactory.generateECKeyPair()
        val transaction = Transaction()
        transaction.addInput("prevTxHash".toByteArray(), 5)
        transaction.addOutput(10.00, keyPair.public as ECPublicKey)

        val hexString = transaction.getRawDataToSign(0)?.toHex()
        println("tx raw (HEX): $hexString")
    }
}
