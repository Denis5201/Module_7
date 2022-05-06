package com.example.module7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.module7.databinding.ActivityIdeBinding
import java.util.*

class IDEActivity : AppCompatActivity() {
    private lateinit var binding : ActivityIdeBinding
    private val adapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        /*binding.buttonRun.setOnClickListener {
            if (binding.textView.text.isNotEmpty()) {
                val variableArr = mutableListOf<Variable>(OurInteger("A", false, 10))

                val str = getArray(binding.expression.text.toString(), variableArr)
                //val str= mutableListOf("5","*","(","50","-","5","^","2",")")
                var outString = getResult(toRPN(str))

                outSting += binding.typeVariable.selectedItem.toString()
                val intent = Intent(this, ConsoleActivity::class.java)
                intent.putExtra(Constants.RESULT, outString)
                startActivity(intent)
            }
        }*/
        binding.buttonHelp.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init(){
        binding.apply {
            devArea.layoutManager = LinearLayoutManager(this@IDEActivity)
            devArea.adapter = adapter
            addBlock.setOnClickListener {
                val block = when (spinner.selectedItem.toString()) {
                    "Новая переменная" -> Blocks.Assignment()
                    "Изменить значение" -> Blocks.ChangeVal()
                    "Ввод/вывод" -> Blocks.InputOutput()
                    else -> Blocks.Assignment()
                }
                adapter.addBlock(block)
            }
            val itemTouchHelper = ItemTouchHelper(helper)
            itemTouchHelper.attachToRecyclerView(devArea)
        }
    }
    private val helper = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val from = viewHolder.adapterPosition
            val to = target.adapterPosition

            Collections.swap(adapter.blockList, from, to)
            adapter.notifyItemMoved(from, to)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            adapter.blockList.removeAt(position)
            adapter.notifyItemRemoved(position)
        }

    }
}