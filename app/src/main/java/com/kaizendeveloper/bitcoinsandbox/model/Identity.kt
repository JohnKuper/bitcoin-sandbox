package com.kaizendeveloper.bitcoinsandbox.model

import java.security.PrivateKey
import java.security.PublicKey

class Identity(
    val publicKey: PublicKey,
    val privateKey: PrivateKey,
    val pseudonym: String
)