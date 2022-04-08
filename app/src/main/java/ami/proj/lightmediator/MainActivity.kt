package ami.proj.lightmediator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ami.proj.lightmediator.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var job: Job? = null
    private var transcribeService: TranscribeStreaming? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                binding.submitButton.isEnabled = true
            } else {
                val intent = Intent(this, RequestPermissionActivity::class.java)
                startActivity(intent)
            }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNumberPicker()

        binding.submitButton.setOnClickListener {
            transcribeService = TranscribeStreaming()
            job = CoroutineScope(IO).launch{ kotlin.runCatching { transcribeService?.streaming() }}
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
        numberPicker.minValue = 1
        numberPicker.maxValue = 5
        numberPicker.wrapSelectorWheel = false
    }
}