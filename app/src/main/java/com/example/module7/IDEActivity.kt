package com.example.module7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.module7.databinding.ActivityIdeBinding
import java.lang.Exception
import java.util.*

class IDEActivity : AppCompatActivity() {
    private lateinit var binding : ActivityIdeBinding
    private val adapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.buttonRun.setOnClickListener {
            val outString = try {
                work()
            } catch (e:Exception){
                "Error! "+e.message.toString()
            }

            val intent = Intent(this, ConsoleActivity::class.java)
            intent.putExtra(Constants.RESULT, outString)
            startActivity(intent)
        }
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
            val from = viewHolder.absoluteAdapterPosition
            val to = target.absoluteAdapterPosition

            Collections.swap(adapter.blockList, from, to)
            adapter.notifyItemMoved(from, to)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            adapter.blockList.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }
    private fun work():String {
        val variableArr = mutableListOf<Variable>()
        var i = 0
        var outString = ""
        var str:String
        var name:String

        while (i<adapter.blockList.size) {
            when (adapter.blockList[i]) {
                is Blocks.Assignment -> {
                    str = (adapter.blockList[i] as Blocks.Assignment).expression
                    name = (adapter.blockList[i] as Blocks.Assignment).name
                    str = if (str.isNotEmpty())
                        getResult(toRPN(getArray(str, variableArr, i)))
                    else
                        "0"
                    when ((adapter.blockList[i] as Blocks.Assignment).type) {
                        "Int" -> variableArr.add(OurInteger(name, str.toDouble().toInt()))
                        "Double" -> variableArr.add(OurDouble(name, str.toDouble()))
                    }

                }
                is Blocks.ChangeVal -> {
                    str = (adapter.blockList[i] as Blocks.ChangeVal).expression
                    name = (adapter.blockList[i] as Blocks.ChangeVal).name
                    val temp = variableArr.find { it.name == name }
                    if (temp != null) {
                        if (str.isNotEmpty()) {
                            str = getResult(toRPN(getArray(str, variableArr, i)))
                            when (temp) {
                                is OurInteger -> temp.value = str.toDouble().toInt()
                                is OurDouble -> temp.commonValue = str.toDouble()
                                else -> temp.commonValue = str.toDouble()
                            }
                        }
                    }
                    else return "Error! Изменение несуществующей переменной! Блок $i"
                }
                is Blocks.InputOutput -> {
                    str = (adapter.blockList[i] as Blocks.InputOutput).expression
                    if ((adapter.blockList[i] as Blocks.InputOutput).type == "Output") {
                        val temp = variableArr.find { it.name == str }
                        if (temp != null) {
                            outString += when (temp) {
                                is OurInteger -> temp.value.toString() + "\n"
                                is OurDouble -> temp.commonValue.toString() + "\n"
                                else -> temp.commonValue.toString() + "\n"
                            }
                        }
                        else return "Error! Вывод несуществующей переменной! Блок $i"
                    }
                }
            }
            i++
        }
        return outString
    }
}