package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.db.User
import com.kaizendeveloper.bitcoinsandbox.model.UserManager
import com.kaizendeveloper.bitcoinsandbox.model.UsersViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_create_user.view.userName
import kotlinx.android.synthetic.main.fragment_users.fab
import kotlinx.android.synthetic.main.fragment_users.usersList


class UsersFragment : Fragment() {

    private val usersAdapter = UsersAdapter(mutableListOf())

    private lateinit var usersViewModel: UsersViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        usersViewModel = ViewModelProviders.of(activity!!).get(UsersViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()
        observeViewModel()

        fab.setOnClickListener { showUserCreationDialog() }
    }

    private fun setupRecycler() {
        usersList.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        usersList.adapter = usersAdapter
    }

    private fun observeViewModel() {
        with(usersViewModel) {
            observableUsers.observe(this@UsersFragment, Observer<List<User>> {
                it?.also { usersAdapter.setUsers(it) }
            })
        }
    }

    private fun showUserCreationDialog() {
        AlertDialog.Builder(context!!).apply {
            val dialogView = layoutInflater.inflate(R.layout.dialog_create_user, null, false)
            setView(dialogView)

            setTitle("Create new user:")
            setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            setPositiveButton(android.R.string.ok) { _, _ ->
                val userName = dialogView.userName.text.toString()
                if (userName.isNotBlank()) {
                    createIfAbsent(userName)
                }
            }
        }.create().show()
    }

    private fun createIfAbsent(name: String) {
        UserManager.getByName(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { Toast.makeText(context, "User is already exists", Toast.LENGTH_SHORT).show() }
            .doOnComplete { UserManager.createUser(name, false) }
            .subscribe()
    }

    inner class UsersAdapter(private val users: MutableList<User>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_user, parent, false)
            return ViewHolder(view) { usersViewModel.updateCurrentUserIfNeeded(users[it]) }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(users[position]) {
                holder.name.text = this.name
                holder.isActive.isChecked = this.isCurrent
            }
        }

        override fun getItemCount(): Int {
            return users.size
        }

        fun setUsers(newUsers: List<User>) {
            with(users) {
                clear()
                addAll(newUsers)
                notifyDataSetChanged()
            }
        }

        inner class ViewHolder(view: View, onPositionClicked: (Int) -> Unit) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.userName)
            val isActive: RadioButton = view.findViewById(R.id.radioBtn)

            init {
                itemView.setOnClickListener {
                    onPositionClicked(adapterPosition)
                }
            }
        }
    }
}
