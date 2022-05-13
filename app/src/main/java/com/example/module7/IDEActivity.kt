package com.example.module7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.module7.databinding.ActivityIdeBinding
import java.util.*

class IDEActivity : AppCompatActivity() {
    private lateinit var binding : ActivityIdeBinding
    private val adapter = RecyclerAdapter()
    var textEdit = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK) {
                textEdit=it.data?.getStringExtra(Constants.INPUT).toString()
            }
        }
        init()

        binding.buttonRun.setOnClickListener {
            val outString = try {
                work(launcher)
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
                    "Условие" -> Blocks.IfBlock()
                    "Иначе" -> Blocks.ElseBlock()
                    "Цикл" -> Blocks.WhileBlock()
                    "Конец условия" -> Blocks.EndIf()
                    "Конец цикла" -> Blocks.EndWhile()
                    else -> Blocks.Assignment()
                }
                adapter.addBlock(block)
                if (block is Blocks.IfBlock) adapter.addBlock(Blocks.EndIf())
                if (block is Blocks.WhileBlock) adapter.addBlock(Blocks.EndWhile())
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
    private fun work(launcher: ActivityResultLauncher<Intent>):String {
        val variableArr = mutableListOf<Variable>()
        var i = 0
        var outString = ""
        var name:String

        while (i<adapter.blockList.size) {
            when (adapter.blockList[i]) {
                is Blocks.Assignment -> {
                    assignment(adapter.blockList, variableArr, i)
                }
                is Blocks.ChangeVal -> {
                    changeVal(adapter.blockList, variableArr, i)
                }
                is Blocks.InputOutput -> {
                    name = (adapter.blockList[i] as Blocks.InputOutput).expression
                    if ((adapter.blockList[i] as Blocks.InputOutput).type == "Output") {
                        val temp = variableArr.find { it.name == name }
                        if (temp != null) {
                            outString += when (temp) {
                                is OurInteger -> temp.value.toString() + "\n"
                                is OurDouble -> temp.commonValue.toString() + "\n"
                                is OurBool -> temp.value.toString() + "\n"
                                else -> temp.commonValue.toString() + "\n"
                            }
                        }
                        else return "Error! Вывод несуществующей переменной! Блок $i"
                    }
                    else {
                        launcher.launch(Intent(this@IDEActivity, InputActivity::class.java))
                        val temp = variableArr.find { it.name == name }
                        if (temp != null) {
                            when (temp) {
                            is OurInteger -> temp.value = textEdit.toDouble().toInt()
                            is OurDouble -> temp.commonValue = textEdit.toDouble()
                            is OurBool -> temp.value = textEdit.toDouble().toInt() != 0
                            else -> temp.commonValue = textEdit.toDouble()
                        }}
                    }
                }
                is Blocks.IfBlock -> {
                    i += ifBlock(adapter.blockList, variableArr, i)
                }
                is Blocks.ElseBlock -> {
                    while (i<adapter.blockList.size && adapter.blockList[i] !is Blocks.EndIf) {
                        ++i
                    }
                }
                is Blocks.WhileBlock -> {
                    val res = inLoop(adapter.blockList, variableArr, i)
                    i += res.first
                    outString += res.second
                }
            }
            i++
        }
        return outString
    }
}