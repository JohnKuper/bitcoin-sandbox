package com.kaizendeveloper.bitcoinsandbox.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.kaizendeveloper.bitcoinsandbox.db.entity.BlockEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.BlockWithTransactions

@Dao
interface BlockchainDao {
    @Insert
    fun insert(block: BlockEntity)

    @Transaction
    @Query("SELECT * from blockchain")
    fun getAll(): LiveData<List<BlockWithTransactions>>
}