package com.kaizendeveloper.bitcoinsandbox;

import com.kaizendeveloper.bitcoinsandbox.model.BitCoinPublicKey;
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction;
import com.kaizendeveloper.bitcoinsandbox.transaction.TxHandler;
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXO;
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXOPool;
import com.kaizendeveloper.bitcoinsandbox.util.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static junit.framework.Assert.assertTrue;

public class TxHandlerHardTest {

    private static byte[] genesisPrevBlockHash = "GenesisBlock".getBytes(); // change this to use md5

    private static Map<BitCoinPublicKey, PrivateKey> validKeyPairs = new ConcurrentHashMap<>();
    private static ArrayList<BitCoinPublicKey> validPublicKeys = new ArrayList<>();

    private static void testKey(KeyPair k) throws Exception {
        Signature sigInstance = Signature.getInstance("SHA256withECDSA");
        sigInstance.initSign(k.getPrivate());
        String plaintext = "This is the message being signed";
        sigInstance.update((plaintext).getBytes());
        byte[] signature = sigInstance.sign();

        assertTrue(Cipher.verifySignature(k.getPublic(), plaintext.getBytes(), signature));
    }

    private static void createPoolWithGenesisBlock() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));

        Transaction transaction = new Transaction();
        transaction.addOutput(100.00, validPublicKeys.get(0));
        transaction.addInput(genesisPrevBlockHash, 0);

        byte[] rawToSign = transaction.getRawDataToSign(0);
        transaction.addSignature(Cipher.sign(rawToSign, pk), 0);
        transaction.build();

        byte[] signature = transaction.getInputs().get(0).getSignature();
        assert (Cipher.verifySignature(validPublicKeys.get(0).getPublicKey(), rawToSign, signature));

        UTXO utxo = new UTXO(transaction.getHash(), 0);
        UTXOPool.add(utxo, transaction.getOutputs().get(0));
    }

    private static void createTestKeys() throws Exception {
        for (int i = 0; i < 5; i++) {
            KeyPair k = Cipher.INSTANCE.generateECKeyPair();
            BitCoinPublicKey coinPublicKey = new BitCoinPublicKey((ECPublicKey) k.getPublic());
            validKeyPairs.put(coinPublicKey, k.getPrivate());
            validPublicKeys.add(coinPublicKey);
            testKey(k);
        }
    }

    @BeforeClass
    public static void beforeAll() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Before
    public void setUp() throws Exception {
        createTestKeys();
        createPoolWithGenesisBlock();
    }

    @After
    public void tearDown() {
        UTXOPool.reset();
    }

    @Test
    public void shouldAccReptThisTransaction() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        TxHandler txHandler = new TxHandler();
        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Signature sigInstance = Signature.getInstance("SHA256withECDSA");
        sigInstance.initSign(pk);

        Transaction transaction = new Transaction();
        transaction.addOutput(10, validPublicKeys.get(1));
        transaction.addOutput(90, validPublicKeys.get(0));
        transaction.addInput(UTXOPool.getAllUTXO().get(0).getTxHash(), 0);

        sigInstance.update(transaction.getRawDataToSign(0));
        byte[] signature = sigInstance.sign();

        transaction.addSignature(signature, 0);
        transaction.build();

        assert (txHandler.isValidTx(transaction));

        //txHandler.handleTxs();
    }

    @Test
    public void shouldAcceptThisTransactionWith2Outputs() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        TxHandler txHandler = new TxHandler();
        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Signature sigInstance = Signature.getInstance("SHA256withECDSA");
        sigInstance.initSign(pk);

        Transaction transaction = new Transaction();
        transaction.addOutput(10, validPublicKeys.get(1));
        transaction.addOutput(10, validPublicKeys.get(2));
        transaction.addOutput(80, validPublicKeys.get(0));
        transaction.addInput(UTXOPool.getAllUTXO().get(0).getTxHash(), 0);

        sigInstance.update(transaction.getRawDataToSign(0));
        byte[] signature = sigInstance.sign();

        transaction.addSignature(signature, 0);
        transaction.build();

        assert (txHandler.isValidTx(transaction));
    }

    @Test
    public void shouldFailThisTransactionIncorrectHash() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        TxHandler txHandler = new TxHandler();
        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Signature sigInstance = Signature.getInstance("SHA256withECDSA");
        sigInstance.initSign(pk);

        Transaction transaction = new Transaction();
        transaction.addOutput(10, validPublicKeys.get(1));
        transaction.addOutput(90, validPublicKeys.get(0));
        transaction.addInput("IncorrectHash".getBytes(), 0);

        sigInstance.update(transaction.getRawDataToSign(0));
        byte[] signature = sigInstance.sign();

        transaction.addSignature(signature, 0);
        transaction.build();

        assert (txHandler.isValidTx(transaction) == false);
    }

    @Test
    public void shouldFailThisTransactionInputLesserThanOutput() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        TxHandler txHandler = new TxHandler();
        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Signature sigInstance = Signature.getInstance("SHA256withECDSA");
        sigInstance.initSign(pk);

        Transaction transaction = new Transaction();
        transaction.addOutput(10, validPublicKeys.get(1));
        transaction.addOutput(100, validPublicKeys.get(0));
        transaction.addInput(UTXOPool.getAllUTXO().get(0).getTxHash(), 0);

        sigInstance.update(transaction.getRawDataToSign(0));
        byte[] signature = sigInstance.sign();

        transaction.addSignature(signature, 0);
        transaction.build();

        assert (txHandler.isValidTx(transaction) == false);
    }

    @Test
    public void shouldFailThisTransactionInputUsingSameUTXOTwice() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        TxHandler txHandler = new TxHandler();
        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Signature sigInstance = Signature.getInstance("SHA256withECDSA");
        sigInstance.initSign(pk);

        Transaction transaction = new Transaction();
        transaction.addOutput(10, validPublicKeys.get(1));
        transaction.addOutput(90, validPublicKeys.get(0));
        transaction.addInput(UTXOPool.getAllUTXO().get(0).getTxHash(), 0);
        transaction.addInput(UTXOPool.getAllUTXO().get(0).getTxHash(), 0);

        sigInstance.update(transaction.getRawDataToSign(0));
        byte[] signature = sigInstance.sign();

        transaction.addSignature(signature, 0);
        transaction.addSignature(signature, 1);
        transaction.build();

        assert (txHandler.isValidTx(transaction) == false);
    }

    @Test
    public void shouldFailThisTransactionIncorrectSig() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        TxHandler txHandler = new TxHandler();
        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(1));
        Signature sigInstance = Signature.getInstance("SHA256withECDSA");
        sigInstance.initSign(pk);

        Transaction transaction = new Transaction();
        transaction.addOutput(10, validPublicKeys.get(1));
        transaction.addOutput(90, validPublicKeys.get(0));
        transaction.addInput(UTXOPool.getAllUTXO().get(0).getTxHash(), 0);

        sigInstance.update(transaction.getRawDataToSign(0));
        byte[] signature = sigInstance.sign();

        transaction.addSignature(signature, 0);
        transaction.build();

        assert (txHandler.isValidTx(transaction) == false);
    }

    @Test
    public void shouldAcceptThisTransactionWith3OutputsV2() throws Exception {
        TxHandler txHandler = new TxHandler();
        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs = new ArrayList<>();

        outputs.add(new Tuple<>(new Double(10), validPublicKeys.get(1)));
        outputs.add(new Tuple<>(new Double(80), validPublicKeys.get(0)));
        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Transaction txn = makeTxn(0, 0, outputs, pk);

        assert (txHandler.isValidTx(txn));
    }

    @Test
    public void shouldHandleTxnWithSameInputsTxns() throws Exception {
        TxHandler txHandler = new TxHandler();
        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs = new ArrayList<>();
        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs2 = new ArrayList<>();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(10), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(80), validPublicKeys.get(0)));

        outputs2.add(new Tuple<Double, BitCoinPublicKey>(new Double(10), validPublicKeys.get(3)));
        outputs2.add(new Tuple<Double, BitCoinPublicKey>(new Double(50), validPublicKeys.get(0)));

        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Transaction txn = makeTxn(0, 0, outputs, pk);
        Transaction txn1 = makeTxn(0, 0, outputs2, pk);
        Transaction[] txns = new Transaction[2];
        txns[0] = txn;
        txns[1] = txn1;
        assert (txHandler.handleTxs(txns).length == 1);
    }

    @Test
    public void shouldHandleTxnWithSameTxnsTwice() throws Exception {
        TxHandler txHandler = new TxHandler();
        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs = new ArrayList<>();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(10), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(80), validPublicKeys.get(0)));

        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Transaction txn = makeTxn(0, 0, outputs, pk);
        Transaction[] txns = new Transaction[1];
        txns[0] = txn;
        assert (txHandler.handleTxs(txns).length == 1);
        assert (txHandler.handleTxs(txns).length == 0);
    }

    @Test
    public void shouldHandleTxnWithSimpleTxnsOneAfterAnother() throws Exception {
        TxHandler txHandler = new TxHandler();
        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(10), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(80), validPublicKeys.get(0)));

        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Transaction txn = makeTxn(0, 0, outputs, pk);
        Transaction[] txns = new Transaction[1];
        txns[0] = txn;
        Transaction[] tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 1);
        assert (UTXOPool.getAllUTXO().size() == 2);

        UTXO utxo = new UTXO(tApplied[0].getHash(), 1);
        outputs.clear();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(10), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(60), validPublicKeys.get(0)));
        txn = makeTxnWithUTXO(utxo, outputs, pk);
        txns[0] = txn;
        tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 1);
        assert (UTXOPool.getAllUTXO().size() == 3);

        utxo = new UTXO(tApplied[0].getHash(), 1);
        outputs.clear();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(10), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(50), validPublicKeys.get(0)));
        txn = makeTxnWithUTXO(utxo, outputs, pk);
        txns[0] = txn;
        tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 1);
        assert (UTXOPool.getAllUTXO().size() == 4);

        utxo = new UTXO(tApplied[0].getHash(), 0);
        outputs.clear();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(5), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(5), validPublicKeys.get(0)));
        txn = makeTxnWithUTXO(utxo, outputs, validKeyPairs.get(validPublicKeys.get(1)));
        txns[0] = txn;
        tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 1);
        assert (UTXOPool.getAllUTXO().size() == 5);
    }

    @Test
    public void shouldHandleTxnWithMultipleValidTxns() throws Exception {
        TxHandler txHandler = new TxHandler();
        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(10), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(80), validPublicKeys.get(0)));

        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Transaction txn = makeTxn(0, 0, outputs, pk);
        Transaction[] txns = new Transaction[1];
        txns[0] = txn;
        Transaction[] tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 1);
        assert (UTXOPool.getAllUTXO().size() == 2);

        // now make 4 coins
        UTXO utxo = new UTXO(tApplied[0].getHash(), 1);
        outputs.clear();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(5), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(75), validPublicKeys.get(0)));
        txn = makeTxnWithUTXO(utxo, outputs, pk);

        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs2 = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        utxo = new UTXO(tApplied[0].getHash(), 0);
        outputs2.add(new Tuple<Double, BitCoinPublicKey>(new Double(2), validPublicKeys.get(1)));
        outputs2.add(new Tuple<Double, BitCoinPublicKey>(new Double(2), validPublicKeys.get(0)));
        Transaction txn1 = makeTxnWithUTXO(utxo, outputs2, validKeyPairs.get(validPublicKeys.get(1)));

        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs3 = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        utxo = new UTXO(tApplied[0].getHash(), 0);
        outputs3.add(new Tuple<Double, BitCoinPublicKey>(new Double(3), validPublicKeys.get(1)));
        outputs3.add(new Tuple<Double, BitCoinPublicKey>(new Double(3), validPublicKeys.get(0)));
        Transaction txn2 = makeTxnWithUTXO(utxo, outputs2, validKeyPairs.get(validPublicKeys.get(1)));

        txns = new Transaction[3];
        txns[0] = txn;
        txns[1] = txn1;
        txns[2] = txn2;

        tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 2);
        System.out.print(UTXOPool.getAllUTXO().size());
        assert (UTXOPool.getAllUTXO().size() == 4);
    }

    @Test
    public void shouldHandleTxnWithMultipleValidTxnsAndOneDependantTxn() throws Exception {
        TxHandler txHandler = new TxHandler();
        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(10), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(80), validPublicKeys.get(0)));

        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Transaction txn = makeTxn(0, 0, outputs, pk);
        Transaction[] txns = new Transaction[1];
        txns[0] = txn;
        Transaction[] tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 1);
        assert (UTXOPool.getAllUTXO().size() == 2);

        // now make 4 coins
        UTXO utxo = new UTXO(tApplied[0].getHash(), 1);
        outputs.clear();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(5), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(75), validPublicKeys.get(0)));
        txn = makeTxnWithUTXO(utxo, outputs, pk);

        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs2 = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        utxo = new UTXO(tApplied[0].getHash(), 0);
        outputs2.add(new Tuple<Double, BitCoinPublicKey>(new Double(2), validPublicKeys.get(1)));
        outputs2.add(new Tuple<Double, BitCoinPublicKey>(new Double(2), validPublicKeys.get(0)));
        Transaction txn1 = makeTxnWithUTXO(utxo, outputs2, validKeyPairs.get(validPublicKeys.get(1)));

        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs3 = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        utxo = new UTXO(txn1.getHash(), 1);
        outputs3.add(new Tuple<Double, BitCoinPublicKey>(new Double(1), validPublicKeys.get(1)));
        Transaction txn2 = makeTxnWithUTXO(utxo, outputs3, validKeyPairs.get(validPublicKeys.get(0)));

        txns = new Transaction[3];
        txns[0] = txn;
        txns[1] = txn1;
        txns[2] = txn2;

        tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 3);
        System.out.print(UTXOPool.getAllUTXO().size());
        assert (UTXOPool.getAllUTXO().size() == 4);
    }

    @Test
    public void shouldHandleTxnWithMultipleValidTxnsAndTwoDependantTxn() throws Exception {
        TxHandler txHandler = new TxHandler();
        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(10), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(80), validPublicKeys.get(0)));

        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Transaction txn = makeTxn(0, 0, outputs, pk);
        Transaction[] txns = new Transaction[1];
        txns[0] = txn;
        Transaction[] tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 1);
        assert (UTXOPool.getAllUTXO().size() == 2);

        // now make 4 coins
        UTXO utxo = new UTXO(tApplied[0].getHash(), 1);
        outputs.clear();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(5), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(75), validPublicKeys.get(0)));
        txn = makeTxnWithUTXO(utxo, outputs, pk);

        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs2 = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        utxo = new UTXO(tApplied[0].getHash(), 0);
        outputs2.add(new Tuple<Double, BitCoinPublicKey>(new Double(2), validPublicKeys.get(1)));
        outputs2.add(new Tuple<Double, BitCoinPublicKey>(new Double(2), validPublicKeys.get(0)));
        Transaction txn1 = makeTxnWithUTXO(utxo, outputs2, validKeyPairs.get(validPublicKeys.get(1)));

        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs3 = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        utxo = new UTXO(txn1.getHash(), 1);
        outputs3.add(new Tuple<Double, BitCoinPublicKey>(new Double(1), validPublicKeys.get(1)));
        Transaction txn2 = makeTxnWithUTXO(utxo, outputs3, validKeyPairs.get(validPublicKeys.get(0)));

        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs4 = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        utxo = new UTXO(txn1.getHash(), 0);
        outputs4.add(new Tuple<Double, BitCoinPublicKey>(new Double(1), validPublicKeys.get(0)));
        Transaction txn3 = makeTxnWithUTXO(utxo, outputs4, validKeyPairs.get(validPublicKeys.get(1)));

        txns = new Transaction[4];
        txns[0] = txn;
        txns[1] = txn1;
        txns[2] = txn2;
        txns[3] = txn3;

        tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 4);
        assert (UTXOPool.getAllUTXO().size() == 4);
    }

    @Test
    public void shouldHandleTxnWithMultipleValidTxnsAndTwoLevelDependantTxn() throws Exception {
        TxHandler txHandler = new TxHandler();
        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(10), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(80), validPublicKeys.get(0)));

        PrivateKey pk = validKeyPairs.get(validPublicKeys.get(0));
        Transaction txn = makeTxn(0, 0, outputs, pk);
        Transaction[] txns = new Transaction[1];
        txns[0] = txn;
        Transaction[] tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 1);
        assert (UTXOPool.getAllUTXO().size() == 2);

        // now make 4 coins
        UTXO utxo = new UTXO(tApplied[0].getHash(), 1);
        outputs.clear();
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(5), validPublicKeys.get(1)));
        outputs.add(new Tuple<Double, BitCoinPublicKey>(new Double(75), validPublicKeys.get(0)));
        txn = makeTxnWithUTXO(utxo, outputs, pk);

        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs2 = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        utxo = new UTXO(tApplied[0].getHash(), 0);
        outputs2.add(new Tuple<Double, BitCoinPublicKey>(new Double(2), validPublicKeys.get(1)));
        outputs2.add(new Tuple<Double, BitCoinPublicKey>(new Double(2), validPublicKeys.get(0)));
        Transaction txn1 = makeTxnWithUTXO(utxo, outputs2, validKeyPairs.get(validPublicKeys.get(1)));

        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs3 = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        utxo = new UTXO(txn1.getHash(), 1);
        outputs3.add(new Tuple<Double, BitCoinPublicKey>(new Double(1), validPublicKeys.get(1)));
        Transaction txn2 = makeTxnWithUTXO(utxo, outputs3, validKeyPairs.get(validPublicKeys.get(0)));

        ArrayList<Tuple<Double, BitCoinPublicKey>> outputs4 = new ArrayList<Tuple<Double, BitCoinPublicKey>>();
        utxo = new UTXO(txn2.getHash(), 0);
        outputs4.add(new Tuple<Double, BitCoinPublicKey>(new Double(0.5), validPublicKeys.get(0)));
        Transaction txn3 = makeTxnWithUTXO(utxo, outputs4, validKeyPairs.get(validPublicKeys.get(1)));

        txns = new Transaction[4];
        txns[0] = txn;
        txns[1] = txn1;
        txns[2] = txn2;
        txns[3] = txn3;

        tApplied = txHandler.handleTxs(txns);

        assert (tApplied.length == 4);
        assert (UTXOPool.getAllUTXO().size() == 4);
    }

    private Transaction makeTxnWithUTXO(UTXO utxo, ArrayList<Tuple<Double, BitCoinPublicKey>> outputs, PrivateKey pk) throws Exception {
        Signature sigInstance = Signature.getInstance("SHA256withECDSA");
        sigInstance.initSign(pk);

        Transaction transaction = new Transaction();
        for (Tuple<Double, BitCoinPublicKey> output : outputs) {
            transaction.addOutput(output.x, output.y);
        }
        transaction.addInput(utxo.getTxHash(), utxo.getIndex());

        sigInstance.update(transaction.getRawDataToSign(0));
        byte[] signature = sigInstance.sign();

        transaction.addSignature(signature, 0);
        transaction.build();

        return transaction;
    }

    private Transaction makeTxn(int inputUTXO, int utxoOutputIndex, ArrayList<Tuple<Double, BitCoinPublicKey>> outputs,
                                PrivateKey pk) throws Exception {
        Signature sigInstance = Signature.getInstance("SHA256withECDSA");
        sigInstance.initSign(pk);

        Transaction transaction = new Transaction();
        for (Tuple<Double, BitCoinPublicKey> output : outputs) {
            transaction.addOutput(output.x, output.y);
        }
        transaction.addInput(UTXOPool.getAllUTXO().get(inputUTXO).getTxHash(), utxoOutputIndex);

        sigInstance.update(transaction.getRawDataToSign(0));
        byte[] signature = sigInstance.sign();

        transaction.addSignature(signature, 0);
        transaction.build();

        return transaction;
    }

    public class Tuple<X, Y> {
        public final X x;
        public final Y y;

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }
}
