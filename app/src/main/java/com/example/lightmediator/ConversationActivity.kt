package com.example.lightmediator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lightmediator.databinding.ActivityConversationBinding
import java.util.ArrayList


class ConversationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val users = intent.extras?.getParcelableArrayList<User>("users")

        // todo temporary
        // display users
        val usersText: List<UsersListView> = users!!.map {
            UsersListView(R.drawable.circle_color_display, it.color, it.name, "User ${it.id}")
        }

        val numbersArrayAdapter = UsersListViewAdapter(
            this,
            usersText as ArrayList<UsersListView>?
        )

        binding.listOfUsers.adapter = numbersArrayAdapter

        binding.endButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}