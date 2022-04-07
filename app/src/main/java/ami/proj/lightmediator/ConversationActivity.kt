package ami.proj.lightmediator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ami.proj.lightmediator.databinding.ActivityConversationBinding
import java.util.ArrayList


class ConversationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val users = intent.extras?.getParcelableArrayList<User>("users")
        val transcribeService = intent.getSerializableExtra("transcribeService") as? TranscribeStreaming

        // todo temporary
        // display users
        val usersText: List<UsersListView> = users!!.map {
            UsersListView(R.drawable.circle_color_display, it.color, it.name, "User ${it.id}", it.getTimeText())
        }

        val usersArrayAdapter = UsersListViewAdapter(
            this,
            usersText as ArrayList<UsersListView>?
        )

        binding.listOfUsers.adapter = usersArrayAdapter

        binding.transcriptButton.setOnClickListener {
            val intent = Intent(this, TranscriptionActivity::class.java)
            intent.putParcelableArrayListExtra("users", users)
            intent.putExtra("transcribeService", transcribeService)
            startActivity(intent)
        }

        binding.endButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}