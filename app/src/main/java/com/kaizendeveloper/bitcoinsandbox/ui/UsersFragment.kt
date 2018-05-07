package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.Observer
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
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.SandboxApplication
import com.kaizendeveloper.bitcoinsandbox.db.User
import com.kaizendeveloper.bitcoinsandbox.model.UserManager
import com.kaizendeveloper.bitcoinsandbox.model.UsersViewModel
import kotlinx.android.synthetic.main.dialog_create_user.view.userName
import kotlinx.android.synthetic.main.fragment_users.fab
import kotlinx.android.synthetic.main.fragment_users.usersList


class UsersFragment : Fragment() {

    private val usersViewModel = UsersViewModel(SandboxApplication.application)
    private val usersAdapter = UsersAdapter(mutableListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateTitle()
        setupRecycler()
        observeViewModel()

        fab.setOnClickListener { showUserCreationDialog() }
    }

    private fun setupRecycler() {
        usersViewModel.allUsers.value?.also {
            usersAdapter.setUsers(it)
        }

        usersList.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        usersList.adapter = usersAdapter
    }

    private fun observeViewModel() {
        usersViewModel.allUsers.observe(this, Observer { users ->
            users?.also { usersAdapter.setUsers(it) }
        })
    }

    private fun updateTitle() {
        val name = UserManager.activeUser.name
        activity?.title = "$name - ${UserManager.getUserBalance()}$"
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
                    UserManager.createUser(userName)
                }
            }
        }.create().show()
    }

    inner class UsersAdapter(private val users: MutableList<User>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

        //TODO Fix it
        private var activeUserPosition = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_user, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(users[position]) {
                holder.name.text = this.name
                if (position == activeUserPosition) {
//                    UserManager.activeUser = users[position]
                    holder.isActive.isChecked = true
                    updateTitle()
                } else {
                    holder.isActive.isChecked = false
                }
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

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.userName)
            val isActive: RadioButton = view.findViewById(R.id.radioBtn)

            init {
                itemView.setOnClickListener {
                    activeUserPosition = adapterPosition
                    notifyDataSetChanged()
                }
            }
        }
    }
}
