package com.kaizendeveloper.bitcoinsandbox.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.model.UserFactory
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.viewPager
import java.util.LinkedList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = BitCoinPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun showUsersDialog() {
        val builderSingle = AlertDialog.Builder(this@MainActivity)
        builderSingle.setTitle("Select One Name:-")

        val arrayAdapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.select_dialog_singlechoice)
        UserFactory.users.forEach {
            arrayAdapter.add(it.name)
        }

        builderSingle.setNegativeButton("cancel",
            { dialog, _ -> dialog.dismiss() })

        builderSingle.setAdapter(arrayAdapter, { dialog, which ->
            UserFactory.activeUser = UserFactory.users[which]
            title = UserFactory.activeUser?.name
        })
        builderSingle.show()
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

    class BlockChainAdapter(private val blocks: LinkedList<Block>) :
        RecyclerView.Adapter<BlockChainAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(TextView(parent.context))
        }

        override fun getItemCount(): Int {
            return blocks.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = blocks[position].hash.toString()
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = itemView as TextView
        }
    }
}
