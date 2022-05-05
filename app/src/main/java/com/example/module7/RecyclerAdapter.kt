package com.example.module7

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.module7.databinding.AssignmentBinding


class RecyclerAdapter:RecyclerView.Adapter<RecyclerAdapter.Holder>() {
    val blockList = ArrayList<Blocks>()

    class Holder(item: View):RecyclerView.ViewHolder(item) {
        var mEditText: EditText = item.findViewById<View>(R.id.nameVariable) as EditText

        constructor(item: View, blockList: ArrayList<Blocks>):this(item) {
            mEditText.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    (blockList[absoluteAdapterPosition] as Blocks.Assignment).name = p0.toString()

                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(blockList[position]) {
            is Blocks.Assignment -> 0
            is Blocks.ChangeVal -> 1
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layout = when(viewType){
            0 -> R.layout.assignment
            1 -> R.layout.change_val
            else -> R.layout.assignment
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return Holder(view, blockList)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when (blockList[position]) {
            is Blocks.Assignment -> {
                holder.mEditText.setText((blockList[position] as Blocks.Assignment).name)
            }
            is Blocks.ChangeVal -> bindChange(blockList[holder.absoluteAdapterPosition] as Blocks.ChangeVal)
        }
    }
    private fun bindAssig(item: Blocks.Assignment, view: View) {
        val binding = AssignmentBinding.bind(view)
        binding.nameVariable.setText(item.name)
        binding.nameVariable.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                item.name = p0.toString()
            }
        })
    }
    private fun bindChange(item: Blocks.ChangeVal) {

    }

    override fun getItemCount(): Int {
        return blockList.size
    }

    fun addBlock(blocks: Blocks){
        blockList.add(blocks)
        notifyItemInserted(blockList.size-1)
    }
}