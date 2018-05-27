package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.db.User
import com.kaizendeveloper.bitcoinsandbox.model.UserManager
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.viewPager

class MainActivity : AppCompatActivity() {

    private lateinit var usersViewModel: UsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usersViewModel = ViewModelProviders.of(this).get(UsersViewModel::class.java)
        usersViewModel.observableCurrentUser.observe(this, Observer<User> {
            it?.also { updateTitle(it) }
        })

        viewPager.adapter = BitCoinPagerAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 4
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun updateTitle(user: User) {
        val balance = String.format("%.2f", UserManager.getUserBalance(user))
        title = "${user.name} - $balance$"
    }

    class BitCoinPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> BlockChainFragment()
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
