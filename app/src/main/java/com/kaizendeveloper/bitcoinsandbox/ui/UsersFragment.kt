package com.kaizendeveloper.bitcoinsandbox.ui

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
import com.kaizendeveloper.bitcoinsandbox.model.User
import com.kaizendeveloper.bitcoinsandbox.model.UserFactory
import kotlinx.android.synthetic.main.dialog_create_user.view.userName
import kotlinx.android.synthetic.main.fragment_users.fab
import kotlinx.android.synthetic.main.fragment_users.usersList


class UsersFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        usersList.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        usersList.adapter = UsersAdapter(UserFactory.users)
        fab.setOnClickListener { showUserCreationDialog() }
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
                    UserFactory.createUser(userName)
                }
            }
        }.create().show()
    }

    class UsersAdapter(private val users: List<User>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

        private var activeUserPosition = users.indexOf(UserFactory.activeUser)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_user, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(users[position]) {
                holder.name.text = name
                holder.userBalance.text = "- 100$"
                if (position == activeUserPosition) {
                    UserFactory.activeUser = users[position]
                    holder.isActive.isChecked = true
                } else {
                    holder.isActive.isChecked = false
                }
            }
        }

        override fun getItemCount(): Int {
            return users.size
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.userName)
            val userBalance: TextView = view.findViewById(R.id.balance)
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
