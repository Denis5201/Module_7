package com.example.module7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.module7.databinding.ActivityConsoleBinding

class ConsoleActivity : AppCompatActivity() {
    private lateinit var binding : ActivityConsoleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConsoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textOut.text = intent.getStringExtra(Constants.RESULT)

        binding.buttonBackC.setOnClickListener {
            finish()
        }
    }
}