package com.kaizendeveloper.bitcoinsandbox

import android.support.test.runner.AndroidJUnit4
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import com.kaizendeveloper.bitcoinsandbox.util.UserTestUtil.Companion.SATOSHI
import com.kaizendeveloper.bitcoinsandbox.util.randomByteArray
import junit.framework.Assert.assertTrue
import org.junit.Assert.assertArrayEquals
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyPair

@RunWith(AndroidJUnit4::class)
class CipherTest {

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

    companion object {

        lateinit var keyPair: KeyPair

        @BeforeClass
        @JvmStatic
        fun setup() {
            keyPair = Cipher.generateECKeyPair(SATOSHI.name)
        }
    }
}
