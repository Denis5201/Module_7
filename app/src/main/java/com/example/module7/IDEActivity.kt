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
    var variableArr = mutableListOf<Variable>()
    var outString = ""
    var textEdit = ""
    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK) {
            textEdit = it.data?.getStringExtra(Constants.INPUT).toString()
            val pos = it.data?.getIntExtra(Constants.POS, 0)
            val resumeWhile = it.data?.getIntExtra(Constants.RESUME, 0)
            resumeWork(pos!!, resumeWhile!!)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
        binding.buttonRun.setOnClickListener {
            variableArr = mutableListOf()
            outString = ""
            try {
                val outString = work(launcher, true)
                if (outString=="input")
                    return@setOnClickListener
                runConsole(outString)
            } catch (e:Exception){
                "Error! "+e.message.toString()
            }
        }
        binding.buttonHelp.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun runConsole(str:String){
        val intent = Intent(this, ConsoleActivity::class.java)
        intent.putExtra(Constants.RESULT, str)
        startActivity(intent)
    }
    private fun resumeWork(pos: Int, resumeWhile: Int){
        val temp = work(launcher, false, pos, resumeWhile)
        if (temp =="input")
            return
        runConsole(outString)
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

    private fun work(launcher: ActivityResultLauncher<Intent>, cond:Boolean,
                     pos:Int = 0, resumeWhile:Int = 0):String {
        var i = pos
        var name:String
        var usl = cond

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
                        if (usl) {
                            val intent = Intent(this, InputActivity::class.java)
                            intent.putExtra(Constants.POS, i)
                            launcher.launch(intent)
                            return "input"
                        }

                        usl = true
                        val temp = variableArr.find { it.name == name }
                        if (temp != null) {
                            if (textEdit.isNotEmpty()){
                            when (temp) {
                                is OurInteger -> temp.value = textEdit.toDouble().toInt()
                                is OurDouble -> temp.commonValue = textEdit.toDouble()
                                is OurBool -> temp.value = textEdit.toDouble().toInt() != 0
                                else -> temp.commonValue = textEdit.toDouble()
                            }
                            }
                        }
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
                    val res = inLoop(adapter.blockList, variableArr, i, textEdit, resumeWhile, usl)
                    outString += res.second
                    if (res.third=="input") {
                        val intent = Intent(this, InputActivity::class.java)
                        intent.putExtra(Constants.POS, i)
                        intent.putExtra(Constants.RESUME, res.first)
                        launcher.launch(intent)
                        return "input"
                    }
                    i += res.first

                }
            }
            i++
        }
        return outString
    }
}