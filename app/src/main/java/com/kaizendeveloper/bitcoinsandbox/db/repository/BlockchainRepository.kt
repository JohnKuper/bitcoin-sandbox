package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import com.kaizendeveloper.bitcoinsandbox.db.dao.BlockchainDao
import com.kaizendeveloper.bitcoinsandbox.db.dao.MempoolDao
import com.kaizendeveloper.bitcoinsandbox.db.entity.BlockEntity
import com.kaizendeveloper.bitcoinsandbox.db.entity.TxEntity
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockchainRepository @Inject constructor(
    private val db: SandboxDatabase,
    private val blockchainDao: BlockchainDao,
    private val mempoolDao: MempoolDao
) {

    val blocks: LiveData<List<Block>> = Transformations.switchMap(blockchainDao.getAll()) { dbBlocks ->
        MutableLiveData<List<Block>>().apply {
            value = dbBlocks.map { it.toBlock() }
        }
    }

    fun insert(block: Block): Completable {
        return Completable.fromAction {
            val blockEntity = BlockEntity.fromBlock(block)
            db.runInTransaction {
                blockchainDao.insert(blockEntity)
                block.transactions.forEach {
                    it.isConfirmed = true
                    mempoolDao.update(TxEntity.fromTransaction(it).apply {
                        parentBlockId = blockEntity.uuid
                    })
                }
            }
        }.subscribeOn(Schedulers.io())
    }

    fun getLastHash(): Single<ByteArray> {
        return blockchainDao
            .getLastBlock()
            .map { it.toBlock().hash }
            .switchIfEmpty(Single.just(Cipher.ZERO_HASH))
    }
}
