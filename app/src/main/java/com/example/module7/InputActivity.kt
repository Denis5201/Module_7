package com.example.module7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.module7.databinding.ActivityInputBinding

class InputActivity : AppCompatActivity() {
    private lateinit var binding : ActivityInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pos = intent.getIntExtra(Constants.POS, 0)
        val resume = intent.getIntExtra(Constants.RESUME, 0)

        binding.enter.setOnClickListener {
            if (binding.textInput.text.toString().isNotEmpty()) {
                val intent = Intent()
                intent.putExtra(Constants.INPUT, binding.textInput.text.toString())
                intent.putExtra(Constants.POS, pos)
                intent.putExtra(Constants.RESUME, resume)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}