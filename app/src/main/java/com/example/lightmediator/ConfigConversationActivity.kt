package com.example.lightmediator

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lightmediator.databinding.ActivityConfigConversationBinding

class ConfigConversationActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityConfigConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val numUsers = intent.getStringExtra("number_users")?.toInt() ?: 2
        binding.displayNumberUsers.text = "Number of users: $numUsers"

        var currentUser = 1
        displayUser(currentUser, numUsers, binding)

        val users = mutableListOf<User>()

        binding.nextButton.setOnClickListener{
            users.add(createUser(currentUser, binding))
            displayUser(++currentUser, numUsers, binding)
        }

    }

    private fun createUser(userId: Int, binding: ActivityConfigConversationBinding): User {
        val name = binding.userId.text ?: "User $userId"

        return User(name.toString(), userId)
    }

    private fun displayUser(currentUser: Int, numUsers: Int, binding: ActivityConfigConversationBinding) {
        binding.userId.hint = "User $currentUser"
        binding.userId.text.clear()
        if (currentUser == numUsers) {
            binding.nextButton.text = "Start Conversation"
            binding.nextButton.isEnabled = false
        }
        else
            binding.nextButton.text = "Next User"
    }

}