package com.kaizendeveloper.bitcoinsandbox.ui.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.ui.viewmodel.UsersViewModel
import com.kaizendeveloper.bitcoinsandbox.util.requireValue

open class UsersViewModelFragment : BaseFragment() {

    protected lateinit var usersViewModel: UsersViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        usersViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(UsersViewModel::class.java)
        usersViewModel.users.observe(this, Observer { users ->
            users?.also { onUsersChanged(users) }
        })
        usersViewModel.currentUser.observe(this, Observer { user ->
            user?.also { onCurrentUserChanged(it) }
        })
    }

    protected fun requireCurrentUser() = usersViewModel.currentUser.requireValue()

    protected open fun onCurrentUserChanged(user: User) {}

    protected open fun onUsersChanged(users: List<User>) {}
}
