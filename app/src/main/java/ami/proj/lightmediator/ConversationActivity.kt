package ami.proj.lightmediator

import ami.proj.lightmediator.databinding.ActivityConversationBinding
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.lang.Double.min


class ConversationActivity : AppCompatActivity() {

    // max contrast color at 30 seconds
    private val TIME_CAP = 30
    private val MAX_PERCENTAGE: Double = 1.0

    private lateinit var binding: ActivityConversationBinding
    private lateinit var transcribeService: TranscribeStreaming
    private lateinit var usersText: List<UsersListView>
    private lateinit var updaterTime: Job
    private var updaterLight: Job? = null

    private val lightInterface: LightInterface? = Store.getInstance().lightInterface


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transcribeService = (intent.getSerializableExtra("transcribeService") as? TranscribeStreaming)!!

        usersText = transcribeService.users.map {
            UsersListView(R.drawable.circle_color_display, it.color, it.name, it.getTimeText())
        }

        updaterTime = updateTime(this)
        if (lightInterface != null) updaterLight = updateLight()

        binding.transcriptButton.setOnClickListener {
            val intent = Intent(this, TranscriptionActivity::class.java)
            intent.putExtra("transcribeService", transcribeService)
            startActivity(intent)
            updaterTime.cancel()
        }

        binding.endButton.setOnClickListener {
            transcribeService.close()

            updaterTime.cancel()
            updaterLight?.cancel()
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        updaterTime = updateTime(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateTime(context: ConversationActivity): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            while(isActive) {
                delay(500)
                // screen changes
                transcribeService.times.zip(usersText).forEach{(time, userText) -> userText.time = time
                }
                val usersArrayAdapter = UsersListViewAdapter(
                    context,
                    usersText as ArrayList<UsersListView>?
                )
                binding.listOfUsers.adapter = usersArrayAdapter
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateLight(): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            while(isActive) {
                delay(1000)
                val usersCopy = transcribeService.users.toList()    // making copy to avoid conflicts
                val maxUser = usersCopy.maxWithOrNull( Comparator.comparingDouble {
                    it.getSpokenTime()
                })

                val minUser = usersCopy.minWithOrNull( Comparator.comparingDouble {
                    it.getSpokenTime()
                })

                if (maxUser == null || minUser == null) continue

                val spokenTime = maxUser.getSpokenTime() - minUser.getSpokenTime()

                lightInterface?.send(getEncodedColor(maxUser, spokenTime))
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getEncodedColor(user: User, time: Double): String {
        val userColor = user.getColor()
        val percentage = 1 - min(time / TIME_CAP, MAX_PERCENTAGE)
        val wantedColor = userColor.map { if (it == 0) (255 * percentage).toInt() else it }

        return wantedColor.joinToString(prefix="{", postfix = "}", separator= ",")

    }
}