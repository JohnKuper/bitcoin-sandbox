package com.kaizendeveloper.bitcoinsandbox.model

import java.security.PrivateKey

data class User(
    val publicKey: BitCoinPublicKey,
    val privateKey: PrivateKey,
    val name: String
)


