package com.kaizendeveloper.bitcoinsandbox.model

import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.db.repository.UsersRepository
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import java.security.interfaces.ECPublicKey
import javax.inject.Inject

class UserManager @Inject constructor(
    private val usersRepo: UsersRepository
) {

    fun createUser(name: String, isCurrent: Boolean = false): User {
        val keyPair = Cipher.generateECKeyPair(name)
        val bitCoinPublicKey = BitCoinPublicKey(keyPair.public as ECPublicKey)
        val user = User(name, bitCoinPublicKey.address, isCurrent)

        usersRepo.insert(user)
        return user
    }
}
