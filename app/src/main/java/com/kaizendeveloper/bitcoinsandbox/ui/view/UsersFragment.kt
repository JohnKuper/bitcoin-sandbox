package com.kaizendeveloper.bitcoinsandbox.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import kotlinx.android.synthetic.main.dialog_new_user.view.userName
import kotlinx.android.synthetic.main.fragment_users.fab
import kotlinx.android.synthetic.main.fragment_users.users_list as userList


class UsersFragment : UsersViewModelFragment() {

    private val usersAdapter = UsersAdapter(mutableListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecycler()

        fab.setOnClickListener { showUserCreationDialog() }
    }

    private fun setupRecycler() {
        userList.apply {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onUsersChanged(users: List<User>) {
        usersAdapter.setUsers(users)
    }

    @SuppressLint("InflateParams")
    private fun showUserCreationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            val dialogView = layoutInflater.inflate(R.layout.dialog_new_user, null, false)
            setView(dialogView)

            setTitle("Create new user:")
            setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            setPositiveButton(android.R.string.ok) { _, _ ->
                val userName = dialogView.userName.text.toString()
                if (userName.isNotBlank()) {
                    createUserIfAbsent(userName)
                }
            }
        }.create().show()
    }

    private fun createUserIfAbsent(name: String) {
        usersViewModel.createUserIfAbsent(name).subscribe()
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
