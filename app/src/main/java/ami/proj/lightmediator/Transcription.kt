package ami.proj.lightmediator

import ami.proj.lightmediator.databinding.ActivityTranscriptionBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.ArrayList

class Transcription : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTranscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val users = intent.extras?.getParcelableArrayList<User>("users")


        binding.backButton.setOnClickListener {
            finish()
        }
    }
}