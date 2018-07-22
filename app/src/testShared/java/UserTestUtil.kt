import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.transaction.TransactionOutput

val SATOSHI = User("Satoshi", "1KoQ3HRxPxHozkKQPRRjbiuVDyFumzYr49", true)
val ALICE = User("Alice", "1KDKkfHmVqyVBZ2iNZr6Fkqwv8ZkTS9nt7", false)
val BOB = User("Bob", "1EHBUQxrndt5WPkv3VtqPtxjJnUmNY9B8w", false)

fun calculateBalance(user: User, utxoPool: List<TransactionOutput>): Double {
    return utxoPool.fold(0.0) { balance, output ->
        if (output.address == user.address) {
            balance + output.amount
        } else {
            balance
        }
    }
}