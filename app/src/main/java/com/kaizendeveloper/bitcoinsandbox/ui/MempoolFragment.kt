package com.kaizendeveloper.bitcoinsandbox.ui

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
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.toHexString
import kotlinx.android.synthetic.main.fragment_mempool.mempoolList

class MempoolFragment : UsersViewModelFragment() {

    private var txsAdapter: TransactionsAdapter = TransactionsAdapter(mutableListOf())
    private lateinit var transactionsViewModel: TransactionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_mempool, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()

        //TODO Delegate this logic to view model
//        fab.setOnClickListener {
//            withCurrentUser { user ->
//                SandboxApplication.m(user)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                        {
//                            Toast.makeText(context, "Block has been minted", Toast.LENGTH_SHORT).show()
//                            SandboxApplication.mempoolRepo.insert(it)
//                        },
//                        {
//                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
//                        })
//            }
//        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        transactionsViewModel = ViewModelProviders.of(this).get(TransactionsViewModel::class.java)
        transactionsViewModel.transactions.observe(this, Observer { txs ->
            txs?.also {
                txsAdapter.setTransactions(it.filter {
                    !it.isConfirmed
                })
            }
        })
    }

    private fun setupRecycler() {
        mempoolList.apply {
            adapter = txsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    //TODO Use ListAdapter for proper UI updates and calculated diffs
    inner class TransactionsAdapter(private val txs: MutableList<Transaction>) :
        RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_transaction, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(txs[position]) {
                holder.txInfo.text = hash!!.toHexString()
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
