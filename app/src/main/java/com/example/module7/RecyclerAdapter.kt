package com.example.module7

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.module7.databinding.AssignmentBinding
import com.example.module7.databinding.ChangeValBinding
import com.example.module7.databinding.InputOutputBinding


class RecyclerAdapter:RecyclerView.Adapter<RecyclerAdapter.Holder>() {
    val blockList = ArrayList<Blocks>()

    class Holder(item: View):RecyclerView.ViewHolder(item) {
        var mEdit1: EditText? = null
        var mEdit2: EditText? = null
        constructor(item: View, blockList: ArrayList<Blocks>, type:Int):this(item) {
            when (type){
                Constants.ASSIGNMENT -> { val binding=AssignmentBinding.bind(item)
                    mEdit1=binding.expression
                    mEdit1!!.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            (blockList[absoluteAdapterPosition] as Blocks.Assignment).expression = p0.toString()
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })
                    mEdit2=binding.nameVariable
                    mEdit2!!.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            (blockList[absoluteAdapterPosition] as Blocks.Assignment).name = p0.toString()
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })

                    binding.typeVariable.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            (blockList[absoluteAdapterPosition] as Blocks.Assignment).type = p0!!.getItemAtPosition(p2).toString()
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }
                }

                Constants.CHANGE -> {val binding=ChangeValBinding.bind(item)
                    mEdit1=binding.expression
                    mEdit1!!.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            (blockList[absoluteAdapterPosition] as Blocks.ChangeVal).expression = p0.toString()
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })
                    mEdit2=binding.nameVariable
                    mEdit2!!.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            (blockList[absoluteAdapterPosition] as Blocks.ChangeVal).name = p0.toString()
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })
                }

                Constants.INOUT -> {val binding=InputOutputBinding.bind(item)
                    mEdit1=binding.expression
                    mEdit1!!.addTextChangedListener(object : TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            (blockList[absoluteAdapterPosition] as Blocks.InputOutput).expression = p0.toString()
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })
                    binding.wayHowToPut.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            (blockList[absoluteAdapterPosition] as Blocks.InputOutput).type = p0!!.getItemAtPosition(p2).toString()
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }
                }
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(blockList[position]) {
            is Blocks.Assignment -> Constants.ASSIGNMENT
            is Blocks.ChangeVal -> Constants.CHANGE
            is Blocks.InputOutput -> Constants.INOUT
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layout = when(viewType){
            Constants.ASSIGNMENT -> R.layout.assignment
            Constants.CHANGE -> R.layout.change_val
            Constants.INOUT -> R.layout.input_output
            else -> R.layout.assignment
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return Holder(view, blockList, viewType)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when (blockList[position]) {
            is Blocks.Assignment -> {
                holder.mEdit2!!.setText((blockList[position] as Blocks.Assignment).name)
                holder.mEdit1!!.setText((blockList[position] as Blocks.Assignment).expression)
            }
            is Blocks.ChangeVal -> {
                holder.mEdit2!!.setText((blockList[position] as Blocks.ChangeVal).name)
                holder.mEdit1!!.setText((blockList[position] as Blocks.ChangeVal).expression)
            }
            is Blocks.InputOutput -> {
                holder.mEdit1!!.setText((blockList[position] as Blocks.InputOutput).expression)
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