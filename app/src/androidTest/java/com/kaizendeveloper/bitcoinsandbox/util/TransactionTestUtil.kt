package com.kaizendeveloper.bitcoinsandbox.util

import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import java.util.Random
import java.util.UUID

fun generateMockTransactions(): List<Transaction> {
    val list = mutableListOf<Transaction>()
    val random = Random()
    repeat(10) {
        list.add(Transaction(random.nextInt(25).toDouble(), UUID.randomUUID().toString()))
    }

    return list
}