package ami.proj.lightmediator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ami.proj.lightmediator.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.lang.Runnable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val audioSource = MediaRecorder.AudioSource.DEFAULT
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_8BIT
    private val bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                println("Permission granted!")
                binding.submitButton.isEnabled = true
            } else {
                println("Permission not granted...")
                val intent = Intent(this, RequestPermissionActivity::class.java)
                startActivity(intent)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNumberPicker()

        binding.submitButton.setOnClickListener {
            val transcribeService = TranscribeStreaming()
            CoroutineScope(IO).launch{transcribeService.streaming()}
            println("\nNow streaming yo\n")
            val intent = Intent(this, ConfigConversationActivity::class.java)
            intent.putExtra("number_users", binding.numberUsersPicker.value.toString())
            intent.putExtra("transcribeService", transcribeService)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) -> {
                binding.submitButton.isEnabled = true
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun setupNumberPicker() {
        val numberPicker = binding.numberUsersPicker
        numberPicker.minValue = 2
        numberPicker.maxValue = 5
        numberPicker.wrapSelectorWheel = false
    }
}