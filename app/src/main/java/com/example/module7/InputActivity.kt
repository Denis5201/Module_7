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

        binding.enter.setOnClickListener {
            if (binding.textInput.text.toString().isNotEmpty()) {
                val intent = Intent()
                intent.putExtra(Constants.INPUT, binding.textInput.text.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}