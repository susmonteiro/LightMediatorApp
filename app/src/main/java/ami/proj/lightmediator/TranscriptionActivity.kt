package ami.proj.lightmediator

import ami.proj.lightmediator.databinding.ActivityTranscriptionBinding
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*


class TranscriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTranscriptionBinding
    private lateinit var transcribeService: TranscribeStreaming

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transcribeService = (intent.getSerializableExtra("transcribeService") as? TranscribeStreaming)!!

        binding.transcriptionText.movementMethod = ScrollingMovementMethod()
        val updater = updateText()

        binding.backButton.setOnClickListener {
            finish()
            updater.cancel()
        }

    }

    private fun updateText(): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            while(isActive) {
                binding.transcriptionText.text = transcribeService.transcription
                delay(500)
            }
        }
    }
}