package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.transaction.Mempool
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.toHex
import kotlinx.android.synthetic.main.fragment_mempool.mempool_list as mempoolList


class MempoolFragment : Fragment() {

    private lateinit var txsAdapter: TransactionsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_mempool, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txsAdapter = TransactionsAdapter(mutableListOf())
        mempoolList.apply {
            adapter = txsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Mempool.observableTransactions.observe(this, Observer { txs ->
            txs?.also { txsAdapter.setTransactions(it) }
        })
    }

    inner class TransactionsAdapter(
        private val txs: MutableList<Transaction>
    ) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.item_transaction, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(txs[position]) {
                holder.txInfo.text = hash!!.toHex()
            }
        }

        override fun getItemCount(): Int {
            return txs.size
        }

        fun setTransactions(txs: List<Transaction>) {
            with(this.txs) {
                clear()
                addAll(txs)
                notifyDataSetChanged()
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val txInfo: TextView = view.findViewById(R.id.tx_info)
        }
    }
}
