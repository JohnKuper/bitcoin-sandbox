package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.kaizendeveloper.bitcoinsandbox.model.BitCoinPublicKey
import java.security.PrivateKey

@Entity(tableName = "users")
class User(
    @Ignore
    val publicKey: BitCoinPublicKey? = null,
    @Ignore
    val privateKey: PrivateKey? = null,

    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "name")
    var name: String
) {

    constructor(id: Int, name: String) : this(null, null, id, name)
}
