package com.kaizendeveloper.bitcoinsandbox


class ECTest {

    //TODO Fix all tests. THEY ARE WRONG!!!
//    @Test
//    fun generateTestAddress() {
//        val keyPair = Cipher.generateECKeyPair2()
//        val pubKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
//        pubKey.address
//    }
//
//    @Test
//    fun signatureShouldBeValid() {
//        val keyPair = Cipher.generateECKeyPair2()
//        val toSign = "BitCoin".toByteArray()
//
//        assertTrue(Cipher.verifySignature(keyPair.public, toSign, Cipher.sign(toSign, keyPair.private)))
//    }
//
//    @Test
//    fun testRawDataToSign() {
//        val transaction = Transaction()
//        transaction.addInput("prevTxHash".toByteArray(), 0)
//        transaction.addOutput(10.00, "some_address")
//
//        val expected = transaction.getRawDataToSign(0)
//    }
//
//    @Test
//    fun testRawTx() {
//        val keyPair = Cipher.generateECKeyPair2()
//        val bitCoinPubKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
//        val transaction = Transaction()
//        transaction.addInput("prevTxHash".toByteArray(), 0)
//        transaction.addInput("prevTxHash2".toByteArray(), 1)
//        transaction.addOutput(10.00, "some_address")
//        transaction.addOutput(15.00, "some_address")
//
//        val firstRawToSign = transaction.getRawDataToSign(0)
//        val secondRawToSign = transaction.getRawDataToSign(1)
//        transaction.addSignature(Cipher.sign(firstRawToSign, keyPair.private), 0)
//        transaction.addSignature(Cipher.sign(secondRawToSign, keyPair.private), 1)
//
//        val block = Block("prevBlockHash".toByteArray())
//        block.add(transaction)
//    }
//
//    @Test
//    fun testByteArrayOutputStream() {
//        val keyPair = Cipher.generateECKeyPair2()
//        val bitCoinPubKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
//        val transaction = Transaction()
//        transaction.addInput("prevTxHash".toByteArray(), 0)
//        transaction.addOutput(10.00, "some_address")
//        val rawToSign = transaction.getRawDataToSign(0)
//        transaction.addSignature(Cipher.sign(rawToSign, keyPair.private), 0)
//    }
//
//    @Test
//    fun genesisTransaction() {
//        val satoshi = UserManager.createUser("Satoshi")
//        val tx = Transaction(25.00, "some_address")
//        val genesisBlock = Block().apply { add(tx) }
//        BlockChain.addBlock(genesisBlock)
//
//        assertTrue(UTXOPool.size() == 1)
//        assertNotNull(UTXOPool.get(UTXO(tx.hash!!, 0)))
//
//        val alice = UserManager.createUser("Alice")
//        val toAliceTx = Transaction()
//        toAliceTx.addInput(tx.hash!!, 0)
//        toAliceTx.addOutput(12.00, "some_address")
//        toAliceTx.addOutput(13.00,"some_address")
//
////        val sig = Cipher.sign(toAliceTx.getRawDataToSign(0), satoshi.privateKey!!)
////        toAliceTx.addSignature(sig, 0)
//        toAliceTx.build()
//        BlockChain.addBlock(Block().apply {
//            add(toAliceTx)
//        })
//
//        assertTrue(UTXOPool.size() == 2)
//        assertNotNull(UTXOPool.get(UTXO(toAliceTx.hash!!, 0)))
//        assertNotNull(UTXOPool.get(UTXO(toAliceTx.hash!!, 1)))
//
//        //Testing user balance
//        val satoshiBalance = UTXOPool.getAllTxOutputs().fold(0.0) { balance, output ->
//            if (output.address == satoshi.address) {
//                balance + output.amount
//            } else {
//                balance
//            }
//        }
//        assertTrue(satoshiBalance == 13.00)
//    }
//
//    companion object {
//
//        private val keyPairs = HashMap<PublicKey, PrivateKey>()
//
//        @BeforeClass
//        @JvmStatic
//        fun beforeAll() {
//            Security.addProvider(BouncyCastleProvider())
//
//            repeat(5) {
//                val keyGen = KeyPairGenerator.getInstance("RSA")
//                val k = keyGen.generateKeyPair()
//                keyPairs[k.public] = k.private
//            }
//        }
//    }
}
