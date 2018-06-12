package com.kaizendeveloper.bitcoinsandbox

import android.support.test.runner.AndroidJUnit4
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CipherTest {

    @Test
    fun decodedPublicKey() {

        val keyPair = Cipher.generateECKeyPair("test")
        val message = "Something to sign".toByteArray()
        val signature = Cipher.sign(message, keyPair.private)

        val decodedPublicKey = Cipher.decodePublicKey(keyPair.public.encoded)
        assertTrue(Cipher.verifySignature(decodedPublicKey, message, signature))
    }
}
