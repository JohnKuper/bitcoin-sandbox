package com.kaizendeveloper.bitcoinsandbox.util

import com.kaizendeveloper.bitcoinsandbox.db.entity.User

const val SATOSHI_NAME = "Satoshi"
const val ALICE_NAME = "Alice"
const val BOB_NAME = "Bob"

val SATOSHI = User(SATOSHI_NAME, "1KoQ3HRxPxHozkKQPRRjbiuVDyFumzYr49", true)
val ALICE = User(ALICE_NAME, "1KDKkfHmVqyVBZ2iNZr6Fkqwv8ZkTS9nt7", false)
val BOB = User(BOB_NAME, "1EHBUQxrndt5WPkv3VtqPtxjJnUmNY9B8w", false)
