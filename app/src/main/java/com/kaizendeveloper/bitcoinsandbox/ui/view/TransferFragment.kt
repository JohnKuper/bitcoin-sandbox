package com.kaizendeveloper.bitcoinsandbox.ui.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.ui.viewmodel.TransactionsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_transfer.amount
import kotlinx.android.synthetic.main.fragment_transfer.fab
import kotlinx.android.synthetic.main.fragment_transfer.sender
import kotlinx.android.synthetic.main.fragment_transfer.spinner_recipient as spinnerRecipient

class TransferFragment : UsersViewModelFragment() {

    private lateinit var transactionsViewModel: TransactionsViewModel
    private lateinit var spinnerAdapter: ArrayAdapter<User>

    private val transferAmount: Double
        get() = amount.text.takeIf { it.isNotEmpty() }?.toString()?.toDouble() ?: 0.0

    private val recipient: User
        get() = spinnerRecipient.selectedItem as User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_transfer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        transactionsViewModel =
                ViewModelProviders.of(requireActivity(), viewModelFactory).get(TransactionsViewModel::class.java)
        transactionsViewModel.operationInProgress.observe(this, Observer { setUiBlocked(it!!) })

        spinnerAdapter = UsersSpinnerAdapter(requireActivity())
        spinnerRecipient.adapter = spinnerAdapter

        fab.setOnClickListener {
            sendCoins()
        }
    }

    private fun setUiBlocked(isBlocked: Boolean) {
        if (isBlocked) {
            ProgressFragment.show(requireFragmentManager())
        } else {
            ProgressFragment.hide(requireFragmentManager())
            fab.isEnabled = true
        }
    }

    private fun sendCoins() {
        withCurrentUser { user ->
            if (transferAmount > 0) {
                fab.isEnabled = false
                transactionsViewModel.sendCoins(transferAmount, user, recipient)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { showSuccessTransfer() },
                        { amount.error = "Not enough coins!" }
                    )
            }
        }
    }

    private fun showSuccessTransfer() {
        amount.error = null
        Toast.makeText(context, "Coins has been sent", Toast.LENGTH_SHORT).show()
    }

    override fun onCurrentUserChanged(user: User) {
        sender.text = user.name
    }

    override fun onUsersChanged(users: List<User>) {
        updateSpinner(users)
    }

    private fun updateSpinner(users: List<User>) {
        spinnerAdapter.clear()
        spinnerAdapter.addAll(users.filter { !it.isCurrent })
        spinnerAdapter.notifyDataSetChanged()
    }

    inner class UsersSpinnerAdapter(context: Context) : ArrayAdapter<User>(context, 0) {

        private val inflater = LayoutInflater.from(context)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return createViewFromResource(position, convertView, parent, android.R.layout.simple_spinner_item)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return createViewFromResource(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item)
        }

        private fun createViewFromResource(
            position: Int,
            convertView: View?,
            parent: ViewGroup?,
            @LayoutRes resource: Int
        ): View {
            val view: TextView = (convertView ?: inflater.inflate(resource, parent, false)) as TextView
            view.text = getItem(position).name
            return view
        }
    }
}
