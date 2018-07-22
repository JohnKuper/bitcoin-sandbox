package com.kaizendeveloper.bitcoinsandbox

import SATOSHI
import android.support.test.runner.AndroidJUnit4
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.randomByteArray
import junit.framework.Assert.assertTrue
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CipherTest {

    private val keyPair = Cipher.generateECKeyPair(SATOSHI.name)

    @Test
    fun verifySignature() {
        val message = randomByteArray()
        val signature = Cipher.sign(message, keyPair.private)

        assertTrue(Cipher.verifySignature(keyPair.public, message, signature))
    }

    @Test
    fun retrieveKeyPair() {
        val retrievedKeyPair = Cipher.retrieveKeyPair(SATOSHI)
        assertArrayEquals(keyPair.public.encoded, retrievedKeyPair.public.encoded)
        assertArrayEquals(keyPair.private.encoded, retrievedKeyPair.private.encoded)
    }

    @Test
    fun decodePublicKey() {
        val decodedPublicKey = Cipher.decodePublicKey(keyPair.public.encoded)
        assertArrayEquals(keyPair.public.encoded, decodedPublicKey.encoded)
    }
}
