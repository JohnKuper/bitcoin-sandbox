package com.kaizendeveloper.bitcoinsandbox

import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXOPool
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID


class TransactionComposerTest {

    private val amountList = listOf(3.5, 2.1, 4.22, 5.99, 5.0, 0.5, 9.99, 11.11, 2.456, 2.457)

    @Before
    fun setup() {
        amountList.forEach {
            UTXOPool.add(
                UTXO(UUID.randomUUID().toString().toByteArray(), 0),
                TransactionOutput(it, "Alice").also {
                })
            UTXOPool.add(
                UTXO(UUID.randomUUID().toString().toByteArray(), 0),
                TransactionOutput(it, "Bob")
            )
        }
    }

    @After
    fun tearDown() {
        UTXOPool.reset()
    }

    @Test
    fun utxoForSendingTest() {
        val sendAmount = 11.55
        var accumulatedAmount = 0.0

        val utxoToSend = UTXOPool.unspentOutputMap
            .asSequence()
            .filter { it.value.address == "Alice" }
            .sortedBy { it.value }
            .takeWhile { accumulatedAmount <= sendAmount }
            .onEach { accumulatedAmount += it.value.amount }
            .onEach {
                println("Sorted ${it.value.address} value = ${it.value.amount}, accumulated = $accumulatedAmount")
            }
            .map { it.key }
            .toList()

        val resultAmount = if (accumulatedAmount >= sendAmount) accumulatedAmount else Double.MIN_VALUE
        val result = Pair(resultAmount, utxoToSend)

        println(result.toString())
    }
}
