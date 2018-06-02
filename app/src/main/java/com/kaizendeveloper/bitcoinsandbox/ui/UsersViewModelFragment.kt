package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import com.kaizendeveloper.bitcoinsandbox.db.entity.User


open class UsersViewModelFragment : Fragment() {

    protected lateinit var usersViewModel: UsersViewModel
    protected lateinit var currentUser: User

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        usersViewModel = ViewModelProviders.of(requireActivity()).get(UsersViewModel::class.java)
        usersViewModel.observableUsers.observe(this, Observer { users ->
            users?.also {
                currentUser = users.first { it.isCurrent }
                onUsersChanged(users)
            }
        })
    }

    protected open fun onUsersChanged(users: List<User>) {}
}
