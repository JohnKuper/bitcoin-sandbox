package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.util.observeOnce


open class UsersViewModelFragment : Fragment() {

    protected lateinit var usersViewModel: UsersViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        usersViewModel = ViewModelProviders.of(requireActivity()).get(UsersViewModel::class.java)
        usersViewModel.users.observe(this, Observer { users ->
            users?.also { onUsersChanged(users) }
        })
        usersViewModel.currentUser.observe(this, Observer { user ->
            user?.also { onCurrentUserChanged(it) }
        })
    }

    protected fun withCurrentUser(block: (User) -> Unit) {
        usersViewModel.currentUser.observeOnce {
            block(it!!)
        }
    }

    protected open fun onCurrentUserChanged(user: User) {}

    protected open fun onUsersChanged(users: List<User>) {}
}
