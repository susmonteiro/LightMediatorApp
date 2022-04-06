package ami.proj.lightmediator

import ami.proj.lightmediator.databinding.ActivityTranscriptionBinding
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class TranscriptionActivity : AppCompatActivity() {

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    private var audioSource = MediaRecorder.AudioSource.DEFAULT
    private var sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_8BIT
    private val bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTranscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val users = intent.extras?.getParcelableArrayList<User>("users")
        val transcribeService = intent.getSerializableExtra("transcribeService") as? TranscribeStreaming

        binding.backButton.setOnClickListener {
            finish()
        }
    }


}