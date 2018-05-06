package com.kaizendeveloper.bitcoinsandbox.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.kaizendeveloper.bitcoinsandbox.R
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.viewPager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = BitCoinPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }

    class BitCoinPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> BlockChainFragment()
                1 -> TransactionsFragment()
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
