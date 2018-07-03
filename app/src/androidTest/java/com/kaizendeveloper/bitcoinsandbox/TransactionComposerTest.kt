package com.kaizendeveloper.bitcoinsandbox


class TransactionComposerTest {

//    private val amountList = listOf(3.5, 2.1, 4.22, 5.99, 5.0, 0.5, 9.99, 11.11, 2.456, 2.457)
//    private val utxoPool = UTXOPool(InstrumentationRegistry.getContext())
//
//    @Before
//    fun setup() {
//        amountList.forEach {
//            utxoPool.add(
//                UTXO(generateRandomHash().wrap(), 0),
//                TransactionOutput(it, "Alice").also {
//                })
//            utxoPool.add(
//                UTXO(generateRandomHash().wrap(), 0),
//                TransactionOutput(it, "Bob")
//            )
//        }
//    }
//
//    @After
//    fun tearDown() {
//        utxoPool.reset()
//    }
//
//    @Test
//    fun utxoForSendingTest() {
//        val sendAmount = 11.55
//        var accumulatedAmount = 0.0
//
//        val utxoToSend = utxoPool.unspentOutputMap
//            .asSequence()
//            .filter { it.value.address == "Alice" }
//            .sortedBy { it.value }
//            .takeWhile { accumulatedAmount <= sendAmount }
//            .onEach { accumulatedAmount += it.value.amount }
//            .onEach {
//                println("Sorted ${it.value.address} value = ${it.value.amount}, accumulated = $accumulatedAmount")
//            }
//            .map { it.key }
//            .toList()
//
//        val resultAmount = if (accumulatedAmount >= sendAmount) accumulatedAmount else Double.MIN_VALUE
//        val result = Pair(resultAmount, utxoToSend)
//
//        println(result.toString())
//    }
//
//    //TODO This is needed in many places. Move to separate class
//    private fun generateRandomHash() = UUID.randomUUID().toString().toByteArray()
}
