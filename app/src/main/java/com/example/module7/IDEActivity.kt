package com.example.module7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.get
import com.example.module7.databinding.ActivityIdeBinding

class IDEActivity : AppCompatActivity() {
    private lateinit var binding : ActivityIdeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRun.setOnClickListener {
            if (binding.textView.text.isNotEmpty()) {
                val variableArr = mutableListOf<Variable>(OurInteger("num", false, 10))

                val str = getArray(binding.expression.text.toString(), variableArr)
                //val str= mutableListOf("5","*","(","50","-","5","^","2",")")
                var outSting = getResult(toRPN(str))
                outSting += binding.typeVariable.selectedItem.toString()
                val intent = Intent(this, ConsoleActivity::class.java)
                intent.putExtra(Constants.RESULT, outSting)
                startActivity(intent)
            }
        }
        binding.buttonHelp.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }
    }
}