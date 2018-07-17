import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import java.util.Random
import java.util.UUID

fun generateMockTransactions(count: Int = 10): List<Transaction> {
    val list = mutableListOf<Transaction>()
    val random = Random()
    repeat(count) {
        list.add(Transaction(random.nextInt(25).toDouble(), UUID.randomUUID().toString()))
    }

    return list
}