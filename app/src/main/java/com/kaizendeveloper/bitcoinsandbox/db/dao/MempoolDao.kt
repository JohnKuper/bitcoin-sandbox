package com.kaizendeveloper.bitcoinsandbox.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxEntityWithRelationData
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxInputEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxOutputEntity

@Dao
interface MempoolDao {
    @Insert
    fun insert(transaction: TxEntity)

    @Insert
    fun insert(transaction: TxInputEntity)

    @Insert
    fun insert(transaction: TxOutputEntity)

    @Transaction
    @Query("SELECT * from mempool_transactions")
    fun getAll(): List<TxEntityWithRelationData>
}