package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
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
        viewPager.adapter = BitCoinPagerAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 4
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

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> BlockchainFragment()
                1 -> MempoolFragment()
                2 -> TransferFragment()
                3 -> UsersFragment()
                else -> throw IllegalStateException("Illegal pager position")
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Block chain"
                1 -> "Transactions"
                2 -> "Transfer"
                3 -> "Users"
                else -> throw IllegalStateException("Illegal pager position")
            }
        }

        override fun getCount() = 4
    }
}
