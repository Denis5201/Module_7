package com.example.module7

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter:RecyclerView.Adapter<RecyclerAdapter.Holder>() {
    val blockList = ArrayList<Blocks>()

    class Holder(item: View):RecyclerView.ViewHolder(item) {
        fun bind(blocks: Blocks) {}
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layout = when(viewType){
            0 -> R.layout.assignment
            1 -> R.layout.change_val
            else -> R.layout.assignment
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return Holder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return when(blockList[position]) {
            is Blocks.Assignment -> 0
            is Blocks.Change_val -> 1
        }
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(blockList[position])
    }

    override fun getItemCount(): Int {
        return blockList.size
    }

    fun addBlock(blocks: Blocks){
        blockList.add(blocks)
        notifyDataSetChanged()
    }
}