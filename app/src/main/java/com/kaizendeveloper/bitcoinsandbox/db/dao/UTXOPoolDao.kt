package com.kaizendeveloper.bitcoinsandbox.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.kaizendeveloper.bitcoinsandbox.db.entity.UTXOWithTxOutput

@Dao
interface UTXOPoolDao {
    @Insert
    fun insert(utxo: UTXOWithTxOutput)

    @Query("DELETE FROM utxo_pool WHERE txHash = :txHash AND outputIndex = :outputIndex")
    fun delete(txHash: ByteArray, outputIndex: Int)

    @Query("SELECT * from utxo_pool")
    fun getAll(): LiveData<List<UTXOWithTxOutput>>
}
