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
import android.widget.Toast
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.transaction.ProgressStatus
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.util.toHexString
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_mempool.fab
import kotlinx.android.synthetic.main.fragment_mempool.mempoolList

class MempoolFragment : UsersViewModelFragment() {

    private lateinit var transactionsViewModel: TransactionsViewModel

    private var txsAdapter: TransactionsAdapter = TransactionsAdapter(mutableListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_mempool, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()

        fab.setOnClickListener {
            withCurrentUser { user ->
                transactionsViewModel.mine(user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { Toast.makeText(context, "Block has been minted", Toast.LENGTH_SHORT).show() },
                        { Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show() }
                    )
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        transactionsViewModel =
                ViewModelProviders.of(requireActivity(), viewModelFactory).get(TransactionsViewModel::class.java)
        transactionsViewModel.transactionStatus
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    ProgressStatus.COMPLETED -> setUiBlocked(false)
                    ProgressStatus.IN_PROGRESS -> setUiBlocked(true)
                }
            }
        transactionsViewModel.transactions.observe(this, Observer { txs ->
            txs?.also {
                txsAdapter.setTransactions(it.filter {
                    !it.isConfirmed
                })
            }
        })
    }

    private fun setUiBlocked(isBlocked: Boolean) {
        fab.isEnabled = !isBlocked
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
