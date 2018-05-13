package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.db.User
import com.kaizendeveloper.bitcoinsandbox.model.UsersViewModel
import kotlinx.android.synthetic.main.fragment_transfer.sender
import kotlinx.android.synthetic.main.fragment_transfer.spinnerRecipient


class TransferFragment : Fragment() {

    private lateinit var spinnerAdapter: ArrayAdapter<User>
    private lateinit var usersViewModel: UsersViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_transfer, container, false)
        usersViewModel = ViewModelProviders.of(activity!!).get(UsersViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()

        spinnerAdapter = UsersSpinnerAdapter(activity!!)
        spinnerRecipient.adapter = spinnerAdapter
    }

    private fun observeViewModel() {
        usersViewModel.observableCurrentUser.observe(this, Observer {
            sender.text = it?.name
        })
        usersViewModel.observableUsers.observe(this, Observer {
            it?.also { updateSpinner(it) }
        })
    }

    private fun updateSpinner(users: List<User>) {
        spinnerAdapter.clear()
        spinnerAdapter.addAll(users.toMutableList().apply {
            remove(usersViewModel.currentUser)
        })
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
