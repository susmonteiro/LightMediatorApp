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

        var users = arrayListOf<User>()

        binding.nextButton.setOnClickListener {
            users.add(createUser(currentUser, binding))

            if (currentUser == numUsers) {
                var intent = Intent(this, ConversationActivity::class.java)
                intent.putParcelableArrayListExtra("users", users)
                startActivity(intent)
            } else {
                displayUser(++currentUser, numUsers, binding)
            }
        }

    }

    private fun createUser(userId: Int, binding: ActivityConfigConversationBinding): User {
        val input = binding.userId.text
        val name = if (input.isNullOrBlank()) "User $userId" else input

        return User(name.toString(), userId)
    }

    private fun displayUser(
        currentUser: Int,
        numUsers: Int,
        binding: ActivityConfigConversationBinding
    ) {
        binding.userId.hint = "User $currentUser"
        binding.userId.text.clear()
        if (currentUser == numUsers) {
            binding.nextButton.text = "Start Conversation"
        // todo remove me
        //binding.nextButton.isEnabled = false
        } else
            binding.nextButton.text = "Next User"
    }

}