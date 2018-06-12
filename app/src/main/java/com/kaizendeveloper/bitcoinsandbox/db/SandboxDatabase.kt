package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.kaizendeveloper.bitcoinsandbox.db.dao.MempoolDao
import com.kaizendeveloper.bitcoinsandbox.db.dao.UTXOPoolDao
import com.kaizendeveloper.bitcoinsandbox.db.dao.UserDao
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxInputEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxOutputEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput
import com.kaizendeveloper.bitcoinsandbox.db.entity.User

@Database(
    entities = [
        User::class,
        UTXOWithTxOutput::class,
        TxEntity::class,
        TxInputEntity::class,
        TxOutputEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SandboxDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun utxoPoolDao(): UTXOPoolDao
    abstract fun mempoolDao(): MempoolDao

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
