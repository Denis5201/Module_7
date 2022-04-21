package com.example.module7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.module7.databinding.ActivityIdeBinding

class IDEActivity : AppCompatActivity() {
    private lateinit var binding : ActivityIdeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRun.setOnClickListener {
            var outSting = ""
            //RunFunction(outSting)
            val intent = Intent(this, ConsoleActivity::class.java)
            intent.putExtra(Constants.RESULT, outSting)
            startActivity(intent)
        }
        binding.buttonHelp.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }
    }
}