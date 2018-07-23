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
import android.widget.Toast
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.transaction.Transaction
import com.kaizendeveloper.bitcoinsandbox.ui.viewmodel.TransactionsViewModel
import com.kaizendeveloper.bitcoinsandbox.util.formatAmount
import com.kaizendeveloper.bitcoinsandbox.util.hide
import com.kaizendeveloper.bitcoinsandbox.util.show
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_mempool.emptyLabel
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
            transactionsViewModel.mine(requireCurrentUser())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { Toast.makeText(context, "Block has been minted", Toast.LENGTH_SHORT).show() },
                    { Toast.makeText(context, "Mining is failed", Toast.LENGTH_SHORT).show() }
                )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        transactionsViewModel =
                ViewModelProviders.of(requireActivity(), viewModelFactory).get(TransactionsViewModel::class.java)
        transactionsViewModel.operationInProgress.observe(this, Observer { setUiBlocked(it!!) })
        transactionsViewModel.transactions.observe(this, Observer { txs ->
            txs?.also {
                handleTransactions(it)
            }
        })
    }

    private fun handleTransactions(transactions: List<Transaction>) {
        val confirmedTxs = transactions.filterNot { it.isConfirmed || it.isCoinbase }
        confirmedTxs.takeIf { it.isNotEmpty() }?.also {
            hideEmptyView()
            txsAdapter.setTransactions(it)
        } ?: showEmptyView()
    }

    private fun showEmptyView() {
        emptyLabel.show()
        fab.hide()
        mempoolList.hide()
    }

    private fun hideEmptyView() {
        emptyLabel.hide()
        fab.show()
        mempoolList.show()
    }

    private fun setUiBlocked(isBlocked: Boolean) {
        if (isBlocked) {
            ProgressFragment.show(requireFragmentManager())
        } else {
            ProgressFragment.hide(requireFragmentManager())
        }
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

        private val inflater by lazy { LayoutInflater.from(requireContext()) }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.item_transaction, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(txs[position]) {
                val prevTx = transactionsViewModel.getTransactionByHash(inputs[0].txHash.data)
                val ownerAddress = prevTx.outputs[inputs[0].outputIndex].address

                val fromWho = usersViewModel.getUserByAddress(ownerAddress).name
                val toWhom = usersViewModel.getUserByAddress(outputs[0].address).name
                val howMuch = outputs.filterNot { it.address == ownerAddress }
                    .fold(0.0) { acc, txOutput -> acc + txOutput.amount }

                val txInfo = "$fromWho ----> $toWhom (${formatAmount(howMuch)})"
                holder.txInfo.text = txInfo
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
            val txInfo: TextView = view.findViewById(R.id.transferInfo)
        }
    }
}
