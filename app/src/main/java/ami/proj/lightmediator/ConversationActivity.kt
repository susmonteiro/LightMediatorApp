package ami.proj.lightmediator

import ami.proj.lightmediator.databinding.ActivityConversationBinding
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*


class ConversationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConversationBinding
    private lateinit var transcribeService: TranscribeStreaming
    private lateinit var usersText: List<UsersListView>
    private lateinit var updater: Job


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transcribeService = (intent.getSerializableExtra("transcribeService") as? TranscribeStreaming)!!

        usersText = transcribeService.users.map {
            UsersListView(R.drawable.circle_color_display, it.color, it.name, it.getTimeText())
        }

        updater = updateTime(this)

        binding.transcriptButton.setOnClickListener {
            val intent = Intent(this, TranscriptionActivity::class.java)
            intent.putExtra("transcribeService", transcribeService)
            startActivity(intent)
            updater.cancel()
        }

        binding.endButton.setOnClickListener {
            transcribeService.close()

            updater.cancel()
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        updater = updateTime(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateTime(context: ConversationActivity): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            while(isActive) {
                transcribeService.times.zip(usersText).forEach{(time, userText) -> userText.time = time
                }
                val usersArrayAdapter = UsersListViewAdapter(
                    context,
                    usersText as ArrayList<UsersListView>?
                )
                binding.listOfUsers.adapter = usersArrayAdapter
                delay(500)
            }
        }
    }
}