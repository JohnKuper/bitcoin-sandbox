package com.kaizendeveloper.bitcoinsandbox.ui.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.ui.viewmodel.UsersViewModel
import com.kaizendeveloper.bitcoinsandbox.util.formatAmount
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.viewPager
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var usersViewModel: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observeViewModel()
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = BitCoinPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.count
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun observeViewModel() {
        usersViewModel = ViewModelProviders.of(this, viewModelFactory).get(UsersViewModel::class.java)
        usersViewModel.currentUser.observe(this, Observer { user ->
            user?.also { updateTitle(it) }
        })
    }

    private fun updateTitle(user: User) {
        user.also {
            title = "${it.name} - ${formatAmount(user.balance)}"
        }
    }

    class BitCoinPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        private val pagesWithTitles = listOf(
            BlockchainFragment() to "Block chain",
            MempoolFragment() to "Transactions",
            TransferFragment() to "Transfer",
            UsersFragment() to "Users"
        )

        override fun getItem(position: Int) = pagesWithTitles[position].first

        override fun getPageTitle(position: Int) = pagesWithTitles[position].second

        override fun getCount() = pagesWithTitles.size
    }
}
