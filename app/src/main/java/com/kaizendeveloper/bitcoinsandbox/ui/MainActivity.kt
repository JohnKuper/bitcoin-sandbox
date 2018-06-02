package com.kaizendeveloper.bitcoinsandbox.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXOPool
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.viewPager

class MainActivity : AppCompatActivity() {

    private lateinit var usersViewModel: UsersViewModel

    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UTXOPool.addInitListener(object : UTXOPool.OnInitListener {
            override fun onInitializationCompleted() {
                updateTitle()
            }
        })

        usersViewModel = ViewModelProviders.of(this).get(UsersViewModel::class.java)
        usersViewModel.observableUsers.observe(this, Observer { users ->
            users?.first {
                it.isCurrent
            }.also {
                currentUser = it
                updateTitle()
            }
        })

        viewPager.adapter = BitCoinPagerAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 4
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun updateTitle() {
        currentUser?.also {
            val balance = String.format("%.2f", usersViewModel.getUserBalance(it))
            title = "${it.name} - $balance"
        }
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
