package com.kaizendeveloper.bitcoinsandbox.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import android.arch.persistence.room.Update
import com.kaizendeveloper.bitcoinsandbox.db.entity.BlockEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.BlockWithTransactions
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxInputEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxOutputEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxWithRelationData
import io.reactivex.Single

@Dao
interface MempoolDao {
    @Insert
    fun insert(txEntity: TxEntity)

    @Insert
    fun insert(txInputEntity: TxInputEntity)

    @Insert
    fun insert(txOutputEntity: TxOutputEntity)

    @Update
    fun update(txEntity: TxEntity)

    @Insert
    fun insert(block: BlockEntity)

    @Transaction
    @Query("SELECT * from blockchain")
    fun getAllBlocks(): LiveData<List<BlockWithTransactions>>

    @Transaction
    @Query("SELECT * from mempool_transactions")
    fun getAllTransactions(): LiveData<List<TxWithRelationData>>

    @Transaction
    @Query("SELECT * from mempool_transactions WHERE isConfirmed = 0")
    fun getAllUnconfirmed(): Single<List<TxWithRelationData>>
}
