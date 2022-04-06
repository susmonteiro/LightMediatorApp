package ami.proj.lightmediator

import ami.proj.lightmediator.databinding.ActivityTranscriptionBinding
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import java.util.regex.Pattern

private const val SELECT_DEVICE_REQUEST_CODE = 0

class BluetoothActivity : AppCompatActivity() {
    private val requestPermissionLauncher =
            registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    println("Permission granted!")
                    enableBluetooth()
                } else {
                    println("Permission not granted...")
                    // TODO send to page
//                    val intent = Intent(this, RequestPermissionActivity::class.java)
//                    startActivity(intent)
                }
            }
    private val deviceManager: CompanionDeviceManager by lazy {
        getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
    }

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        if (this.bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            // TODO handle
            System.err.println("No Bluetooth available")
            return
        }
    }

    override fun onStart() {
        super.onStart()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                Manifest.permission.BLUETOOTH_CONNECT)
        }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) -> {
                enableBluetooth()
            }
        }

        // To skip filters based on names and supported feature flags (UUIDs),
        // omit calls to setNamePattern() and addServiceUuid()
        // respectively, as shown in the following  Bluetooth example.
        val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
            .setNamePattern(Pattern.compile("HC-06"))
            .build()

        // The argument provided in setSingleDevice() determines whether a single
        // device name or a list of them appears.
        val pairingRequest: AssociationRequest = AssociationRequest.Builder()
            .addDeviceFilter(deviceFilter)
            .setSingleDevice(true)
            .build()

        val searchingText = findViewById<TextView>(R.id.searching_text)
        searchingText.visibility = View.VISIBLE

        // When the app tries to pair with a Bluetooth device, show the
        // corresponding dialog box to the user.
        deviceManager.associate(pairingRequest,
            object : CompanionDeviceManager.Callback() {

                override fun onDeviceFound(chooserLauncher: IntentSender) {
                    searchingText.visibility = View.INVISIBLE
                    startIntentSenderForResult(chooserLauncher,
                        SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0)
                }

                override fun onFailure(error: CharSequence?) {
                    searchingText.visibility = View.INVISIBLE
                    val unableText = findViewById<TextView>(R.id.unable_text)
                    unableText.visibility = View.VISIBLE
                    // Handle the failure.
                }
            }, null)
    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SELECT_DEVICE_REQUEST_CODE -> when(resultCode) {
                Activity.RESULT_OK -> {
                    // The user chose to pair the app with a Bluetooth device.
                    val deviceToPair: BluetoothDevice? =
                        data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
                    deviceToPair?.let { device ->
                        device.createBond()
                        // Maintain continuous interaction with a paired device.
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @SuppressLint("MissingPermission")
    fun enableBluetooth() {
        var REQUEST_ENABLE_BT = 1; // Returned as requestCode, needs to be greater than 0
        if (this.bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)


            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

}