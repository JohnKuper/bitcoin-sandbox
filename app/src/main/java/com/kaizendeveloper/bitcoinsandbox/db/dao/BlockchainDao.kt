package com.kaizendeveloper.bitcoinsandbox.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.kaizendeveloper.bitcoinsandbox.db.entity.BlockEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.BlockWithTransactions
import io.reactivex.Maybe

@Dao
interface BlockchainDao {
    @Insert
    fun insert(block: BlockEntity)

    @Transaction
    @Query("SELECT * FROM blockchain")
    fun getAll(): LiveData<List<BlockWithTransactions>>

    @Transaction
    @Query("SELECT * FROM blockchain ORDER BY timestamp DESC LIMIT 1")
    fun getLastBlock(): Maybe<BlockWithTransactions?>
}