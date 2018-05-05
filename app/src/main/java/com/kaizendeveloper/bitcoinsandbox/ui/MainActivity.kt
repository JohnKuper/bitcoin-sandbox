package com.kaizendeveloper.bitcoinsandbox.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.blockchain.BlockChain
import com.kaizendeveloper.bitcoinsandbox.model.UserFactory
import kotlinx.android.synthetic.main.activity_main.button
import kotlinx.android.synthetic.main.activity_main.recycler
import java.util.LinkedList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = UserFactory.activeUser?.name

        BlockChain.addObserver { _, _ ->
            recycler.adapter.notifyDataSetChanged()
        }

        recycler.layoutManager = LinearLayoutManager(baseContext).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        recycler.adapter = BlockChainAdapter(BlockChain.blocks)

        button.setOnClickListener {
            showUsersDialog()
        }
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
