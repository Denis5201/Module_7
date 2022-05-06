package com.example.module7

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.module7.databinding.AssignmentBinding
import com.example.module7.databinding.ChangeValBinding
import com.example.module7.databinding.InputOutputBinding


class RecyclerAdapter:RecyclerView.Adapter<RecyclerAdapter.Holder>() {
    val blockList = ArrayList<Blocks>()

    class Holder(item: View):RecyclerView.ViewHolder(item) {
        var mEditText1: EditText? = null
        var mEditText2: EditText? = null
        constructor(item: View, blockList: ArrayList<Blocks>, type:Int):this(item) {
            when (type){
                0 -> { val binding=AssignmentBinding.bind(item)
                    mEditText1=binding.expression
                    mEditText1!!.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            (blockList[absoluteAdapterPosition] as Blocks.Assignment).expression = p0.toString()
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })
                    mEditText2=binding.nameVariable
                    mEditText2!!.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            (blockList[absoluteAdapterPosition] as Blocks.Assignment).name = p0.toString()
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })
                }
                1 -> {val binding=ChangeValBinding.bind(item)
                    mEditText1=binding.expression
                    mEditText1!!.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            (blockList[absoluteAdapterPosition] as Blocks.ChangeVal).expression = p0.toString()
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })
                    mEditText2=binding.nameVariable
                    mEditText2!!.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            (blockList[absoluteAdapterPosition] as Blocks.ChangeVal).name = p0.toString()
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })
                }
                2->{val binding=InputOutputBinding.bind(item)
                    mEditText1=binding.expression
                    mEditText1!!.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            (blockList[absoluteAdapterPosition] as Blocks.InputOutput).expression = p0.toString()
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })

                }
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(blockList[position]) {
            is Blocks.Assignment -> 0
            is Blocks.ChangeVal -> 1
            is Blocks.InputOutput -> 2
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layout = when(viewType){
            0 -> R.layout.assignment
            1 -> R.layout.change_val
            2 -> R.layout.input_output
            else -> R.layout.assignment
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return Holder(view, blockList, viewType)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when (blockList[position]) {
            is Blocks.Assignment -> {
                holder.mEditText2!!.setText((blockList[position] as Blocks.Assignment).name)
                holder.mEditText1!!.setText((blockList[position] as Blocks.Assignment).expression)
            }
            is Blocks.ChangeVal -> {
                holder.mEditText2!!.setText((blockList[position] as Blocks.ChangeVal).name)
                holder.mEditText1!!.setText((blockList[position] as Blocks.ChangeVal).expression)
            }
            is Blocks.InputOutput -> {
                holder.mEditText1!!.setText((blockList[position] as Blocks.InputOutput).expression)
            }
        }
    }

    override fun getItemCount(): Int {
        return blockList.size
    }

    fun addBlock(blocks: Blocks){
        blockList.add(blocks)
        notifyItemInserted(blockList.size-1)
    }
}