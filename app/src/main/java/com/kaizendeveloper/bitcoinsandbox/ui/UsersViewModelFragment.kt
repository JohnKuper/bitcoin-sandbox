package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment


open class UsersViewModelFragment : Fragment() {

    protected lateinit var usersViewModel: UsersViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        usersViewModel = ViewModelProviders.of(requireActivity()).get(UsersViewModel::class.java)
    }
}
