package com.example.lightmediator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lightmediator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNumberPicker()

        binding.submitButton.setOnClickListener {
            val intent = Intent(this, ConfigConversationActivity::class.java)
            intent.putExtra("number_users", binding.numberUsersPicker.value.toString())
            startActivity(intent)
        }
    }

    private fun setupNumberPicker() {
        val numberPicker = binding.numberUsersPicker
        numberPicker.minValue = 2
        numberPicker.maxValue = 10
        numberPicker.wrapSelectorWheel = false
    }
}