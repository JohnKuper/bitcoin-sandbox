package com.kaizendeveloper.bitcoinsandbox

import java.security.PrivateKey
import java.security.PublicKey

class Identity(
    private val publicKey: PublicKey,
    private val privateKey: PrivateKey,
    private val pseudonym: String
)