package ami.proj.lightmediator

import ami.proj.lightmediator.databinding.ActivityRequestPermissionBinding
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RequestPermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.requestButton.setOnClickListener {
            finish()
        }
    }
}