package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Maybe

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Query("DELETE FROM users")
    fun deleteAll()

    @Query("SELECT * from users")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * from users WHERE name == :name")
    fun getByName(name: String): Maybe<User>
}
