package com.example.lightmediator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lightmediator.databinding.ActivityConfigConversationBinding

class ConfigConversationActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityConfigConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val numUsers = intent.getStringExtra("number_users")
        binding.displayNumberUsers.text = "Number of users: $numUsers"
    }
}