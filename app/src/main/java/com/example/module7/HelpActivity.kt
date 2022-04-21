package com.example.module7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.module7.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBackH.setOnClickListener {
            finish()
        }
    }
}