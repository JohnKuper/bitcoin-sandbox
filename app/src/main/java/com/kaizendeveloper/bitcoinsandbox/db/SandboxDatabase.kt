package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.kaizendeveloper.bitcoinsandbox.db.dao.UTXOPoolDao
import com.kaizendeveloper.bitcoinsandbox.db.dao.UserDao
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.db.entity.User

@Database(
    entities = [User::class, UTXOWithTxOutput::class],
    version = 1,
    exportSchema = false
)
abstract class SandboxDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun utxoPoolDao(): UTXOPoolDao

    companion object {

        private const val DATABASE_NAME = "bitcoin.db"

        @Volatile
        private var INSTANCE: SandboxDatabase? = null

        fun getInstance(context: Context): SandboxDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SandboxDatabase::class.java, DATABASE_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }
}
