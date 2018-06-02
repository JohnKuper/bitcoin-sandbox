package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    var name: String,
    var address: String,
    var isCurrent: Boolean = false
)
