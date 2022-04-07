package ami.proj.lightmediator

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import ami.proj.lightmediator.databinding.ActivityConfigConversationBinding
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

class ConfigConversationActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "NewApi")

    // todo change colors
    val colors = listOf(
        Color.rgb(0, 0, 255),
        Color.rgb(255, 0, 0),
        Color.rgb(0, 255, 0),
        Color.rgb(255, 255, 0),
        Color.rgb(255, 0, 255)
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityConfigConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val numUsers = intent.getStringExtra("number_users")?.toInt() ?: 2
        val transcribeService = intent.getSerializableExtra("transcribeService") as? TranscribeStreaming
        binding.displayNumberUsers.text = "Number of users: $numUsers"

        var currentUser = 0
        displayUser(currentUser, numUsers, binding)

        val users = arrayListOf<User>()

        binding.nextButton.setOnClickListener {
            users.add(createUser(currentUser, binding, colors[currentUser]))

            if (currentUser == numUsers - 1) {
                transcribeService?.users = users
                val intent = Intent(this, ConversationActivity::class.java)
                intent.putExtra("transcribeService", transcribeService)
                startActivity(intent)
            } else {
                displayUser(++currentUser, numUsers, binding)
            }
        }

    }

    private fun createUser(userId: Int, binding: ActivityConfigConversationBinding, color: Int): User {
        val input = binding.userId.text
        val name = if (input.isNullOrBlank()) "User ${userId + 1}" else input

        return User(name.toString(), userId, color)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun displayUser(
        currentUser: Int,
        numUsers: Int,
        binding: ActivityConfigConversationBinding
    ) {
        with(binding) {
            userId.hint = "User ${currentUser + 1}"
            userId.text.clear()
            circleColor.drawable.colorFilter =
                PorterDuffColorFilter(colors[currentUser], PorterDuff.Mode.SRC_IN)
            if (currentUser == numUsers - 1) {
                nextButton.text = "Start Conversation"
            } else
                nextButton.text = "Next User"
        }
    }

}