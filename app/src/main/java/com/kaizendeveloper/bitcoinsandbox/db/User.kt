package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "users")
data class User(

    @PrimaryKey
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "address")
    var address: String
)
