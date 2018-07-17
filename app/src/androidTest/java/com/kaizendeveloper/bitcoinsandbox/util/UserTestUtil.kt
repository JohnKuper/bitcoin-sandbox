package com.kaizendeveloper.bitcoinsandbox.util

import com.kaizendeveloper.bitcoinsandbox.db.entity.User

class UserTestUtil {

    companion object {
        val SATOSHI = User("Satoshi", "1KoQ3HRxPxHozkKQPRRjbiuVDyFumzYr49", true)
        val ALICE = User("Alice", "1KDKkfHmVqyVBZ2iNZr6Fkqwv8ZkTS9nt7", false)
        val BOB = User("Bob", "1EHBUQxrndt5WPkv3VtqPtxjJnUmNY9B8w", false)
    }
}
