package com.kaizendeveloper.bitcoinsandbox.ui.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.ui.viewmodel.TransactionsViewModel
import com.kaizendeveloper.bitcoinsandbox.util.toHexString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.android.synthetic.main.fragment_blockchain.blockchain_list as blockChainList


class BlockchainFragment : BaseFragment() {

    private val blocksAdapter = BlocksAdapter(mutableListOf())
    private lateinit var transactionsViewModel: TransactionsViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_blockchain, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        transactionsViewModel =
                ViewModelProviders.of(requireActivity(), viewModelFactory).get(TransactionsViewModel::class.java)
        transactionsViewModel.blocks.observe(this, Observer {
            it?.also { blocksAdapter.setBlocks(it) }
        })
    }

    private fun setupRecycler() {
        blockChainList.apply {
            adapter = blocksAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    inner class BlocksAdapter(private val blocks: MutableList<Block>) :
        RecyclerView.Adapter<BlocksAdapter.ViewHolder>() {

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.UK)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_block, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            blocks[position].also {
                val blockInfo = """
                    hash: ${it.hash.toHexString()}
                    parent hash: ${it.prevBlockHash.toHexString()}
                    merkle root: ${it.merkleRoot.toHexString()}
                    timestamp: ${formatTimestamp(it.timestamp)}
                    nonce: ${it.nonce}
                    transactions count: ${it.transactions.filterNot { it.isCoinbase }.size}
                """.trimIndent()

                holder.blockInfo.text = blockInfo
            }
        }

        override fun getItemCount(): Int {
            return blocks.size
        }

        private fun formatTimestamp(timestamp: Long): String {
            val timestampDate = Date(timestamp)
            return dateFormat.format(timestampDate)
        }

        fun setBlocks(newBlocks: List<Block>) {
            with(blocks) {
                clear()
                addAll(newBlocks)
                notifyDataSetChanged()
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val blockInfo: TextView = view.findViewById(R.id.blockInfo)
        }
    }
}
