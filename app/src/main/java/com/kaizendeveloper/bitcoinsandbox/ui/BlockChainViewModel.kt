package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.ViewModel
import com.kaizendeveloper.bitcoinsandbox.blockchain.BlockChain

//TODO Probably should take data from DB, but for now take it directly from BlockChain singleton
class BlockChainViewModel : ViewModel() {

    val blockChain = BlockChain.observableBlocks
}
