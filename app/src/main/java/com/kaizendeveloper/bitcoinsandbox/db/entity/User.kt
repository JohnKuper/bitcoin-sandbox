package com.kaizendeveloper.bitcoinsandbox.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    var name: String,
    @ColumnInfo(name = "address")
    var address: String,
    @ColumnInfo(name = "isCurrent")
    var isCurrent: Boolean = false
) {
    @Ignore
    var balance: Double = 0.0
}
