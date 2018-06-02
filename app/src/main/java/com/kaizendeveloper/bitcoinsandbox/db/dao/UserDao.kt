package com.kaizendeveloper.bitcoinsandbox.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import io.reactivex.Maybe

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Query("SELECT * from users")
    fun getAll(): LiveData<List<User>>

    @Query("SELECT * from users WHERE name = :name")
    fun getByName(name: String): Maybe<User>

    @Query("SELECT * from users WHERE isCurrent = 1")
    fun getCurrent(): LiveData<User>
}
