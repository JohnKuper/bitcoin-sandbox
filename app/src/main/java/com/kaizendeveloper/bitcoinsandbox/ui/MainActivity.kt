package com.kaizendeveloper.bitcoinsandbox.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kaizendeveloper.bitcoinsandbox.R
import com.kaizendeveloper.bitcoinsandbox.blockchain.Block
import com.kaizendeveloper.bitcoinsandbox.blockchain.BlockChain2
import com.kaizendeveloper.bitcoinsandbox.util.Cipher
import kotlinx.android.synthetic.main.activity_main.button
import kotlinx.android.synthetic.main.activity_main.recycler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val keyPair = Cipher.generateECKeyPair()
        val genesisBlock = Block(null)
        genesisBlock.build()
        val blockChain = BlockChain2(genesisBlock)

        blockChain.addObserver { _, _ ->
            recycler.adapter.notifyDataSetChanged()
        }

        recycler.layoutManager = LinearLayoutManager(baseContext).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        recycler.adapter = BlockChainAdapter(blockChain.blocks)

        button.setOnClickListener {
            val secondBlock = Block(genesisBlock.hash)
            secondBlock.build()
            blockChain.addBlockAndNotify(secondBlock)
        }
    }

    class BlockChainAdapter(private val blocks: ArrayList<Block>) :
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
