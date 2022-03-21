package com.example.lightmediator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lightmediator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNumberPicker()
    }

    private fun setupNumberPicker() {
        val numberPicker = binding.numberUsersPicker
        numberPicker.minValue = 2
        numberPicker.maxValue = 10
        numberPicker.wrapSelectorWheel = false
    }
}