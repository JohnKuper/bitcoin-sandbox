package com.kaizendeveloper.bitcoinsandbox

import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.Signature
import java.security.SignatureException


object Crypto {

    /**
     * @return true if `signature` is a valid digital signature of `message` under the
     * key `pubKey`. Internally, this uses RSA signature, but the student does not
     * have to deal with any of the implementation details of the specific signature
     * algorithm
     */
    fun verifySignature(pubKey: PublicKey, message: ByteArray, signature: ByteArray): Boolean {
        var sig: Signature? = null
        try {
            sig = Signature.getInstance("SHA256withRSA")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        try {
            sig!!.initVerify(pubKey)
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }

        try {
            sig!!.update(message)
            return sig.verify(signature)
        } catch (e: SignatureException) {
            e.printStackTrace()
        }

        return false
    }
}