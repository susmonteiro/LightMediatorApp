package ami.proj.lightmediator

import ami.proj.lightmediator.databinding.ActivityMainBinding
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO


val LIGHT_CONNECT_CODE = 10

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var job: Job? = null
    private var transcribeService: TranscribeStreaming? = null
    
    private val audioSource = MediaRecorder.AudioSource.DEFAULT
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_8BIT
    private val bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    private var lightInterface: LightInterface? = Store.getInstance().lightInterface
    private var connectedToLight: Boolean = false

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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNumberPicker()

        binding.submitButton.setOnClickListener {
            transcribeService = TranscribeStreaming()
            job = CoroutineScope(IO).launch{transcribeService?.streaming()}
            val intent = Intent(this, ConfigConversationActivity::class.java)
            intent.putExtra("number_users", binding.numberUsersPicker.value.toString())
            intent.putExtra("transcribeService", transcribeService)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        if (lightInterface?.isConnected() == true) {
            binding.connectButton.isEnabled = false
        }

        println("\nCancel the job please\n")
//        transcribeService?.close()
//        transcribeService = null
//        CoroutineScope(IO).cancel()
        println("\nI stoppped I think\n")

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

    fun submit(view: View) {
        val transcribeService = TranscribeStreaming()
        CoroutineScope(IO).launch{transcribeService.streaming()}
        println("\nNow streaming yo\n")
        val intent = Intent(this, ConfigConversationActivity::class.java)
        intent.putExtra("number_users", binding.numberUsersPicker.value.toString())
        intent.putExtra("transcribeService", transcribeService)
        startActivity(intent)
    }

    fun connect(view: View) {
        if (!connectedToLight) {
            Store.getInstance().lightInterface = LightInterface(this)
            this.lightInterface = Store.getInstance().lightInterface

            val intent = Intent(this,BluetoothActivity::class.java)
            startActivityForResult(intent, LIGHT_CONNECT_CODE)
        } else {
            this.lightInterface!!.send("{255,255,255}")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LIGHT_CONNECT_CODE && resultCode == RESULT_OK) {
            markAsConnectedToLight()
        }
        else if (requestCode == LIGHT_CONNECT_CODE && resultCode != RESULT_OK) {
            Toast.makeText(this, "Connect light result not ok", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun markAsConnectedToLight() {
        connectedToLight = true
        binding.connectButton.isEnabled = false
    }

    override fun finish() {
        super.finish()
//        this.lightInterface?.finish()
    }
}