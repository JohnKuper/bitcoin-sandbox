package com.kaizendeveloper.bitcoinsandbox.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface UTXOPoolDao {
    @Insert
    fun insert(utxo: UTXOWithTxOutput)

    @Query("DELETE FROM utxo_pool WHERE txHash = :txHash AND outputIndex = :outputIndex")
    fun delete(txHash: ByteArray, outputIndex: Int)

    @Query("SELECT * from utxo_pool")
    fun getAll(): List<UTXOWithTxOutput>
}
