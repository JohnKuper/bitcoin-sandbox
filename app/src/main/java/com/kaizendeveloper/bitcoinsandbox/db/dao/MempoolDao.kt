package com.kaizendeveloper.bitcoinsandbox.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxInputEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxOutputEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxWithRelationData

@Dao
interface MempoolDao {
    @Insert
    fun insert(txEntity: TxEntity)

    @Insert
    fun insert(txInputEntity: TxInputEntity)

    @Insert
    fun insert(txOutputEntity: TxOutputEntity)

    @Transaction
    @Query("SELECT * from mempool_transactions")
    fun getAll(): LiveData<List<TxWithRelationData>>
}