package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
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
import com.kaizendeveloper.bitcoinsandbox.util.toHexString
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_blockchain.blockchain_list as blockChainList


class BlockChainFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

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

    //TODO Too many similar adapters. Probably should be improved.
    inner class BlocksAdapter(private val blocks: MutableList<Block>) :
        RecyclerView.Adapter<BlocksAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_block, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(blocks[position]) {
                holder.hash.text = hash.toHexString()
            }
        }

        override fun getItemCount(): Int {
            return blocks.size
        }

        fun setBlocks(blocks: List<Block>) {
            with(this.blocks) {
                clear()
                addAll(blocks)
                notifyDataSetChanged()
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val hash: TextView = view.findViewById(R.id.hash)
        }
    }
}
