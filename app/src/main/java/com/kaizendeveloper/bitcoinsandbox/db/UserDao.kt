package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Maybe

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Query("SELECT * from users")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * from users WHERE name = :name")
    fun getByName(name: String): Maybe<User>

    @Query("SELECT * from users WHERE isCurrent = 1")
    fun getCurrentUser(): LiveData<User>
}
