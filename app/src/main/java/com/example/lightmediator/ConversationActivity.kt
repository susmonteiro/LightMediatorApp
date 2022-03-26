package com.example.lightmediator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.lightmediator.databinding.ActivityConversationBinding

class ConversationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val users = intent.extras?.getParcelableArrayList<User>("users")

        // temporary
        // display users
        val usersText: List<String> = users!!.map { "User ${it.id}: ${it.name}" }
        binding.listOfUsers.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, usersText)

        binding.endButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}