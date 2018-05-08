package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(User::class)], version = 1, exportSchema = false)
abstract class SandboxDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: SandboxDatabase? = null

        fun getInstance(context: Context): SandboxDatabase {
            if (INSTANCE == null) {
                synchronized(SandboxDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        SandboxDatabase::class.java, "bitcoin.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}