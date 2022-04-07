package ami.proj.lightmediator

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

val REQUEST_ENABLE_BT = 1; // Returned as requestCode, needs to be greater than 0

class BluetoothActivity : AppCompatActivity() {

    private var lightInterface = Store.getInstance().lightInterface
    private var MAC: String? = null
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val requestPermissionLauncherConnect =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                println("Permission granted for Connect!")
                bluetoothMain()
            } else {
                println("Permission not granted for connect...")
                Toast.makeText(this, "Need permission to connect to light.", Toast.LENGTH_LONG)
                    .show()
                finish()

            }
        }

    private val requestPermissionLauncherScan =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                println("Permission granted!")
                connect()
            } else {
                println("Permission not granted...")
                Toast.makeText(this, "Need permission to connect to light.", Toast.LENGTH_LONG)
                    .show()
                finish()

            }
        }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) -> {
                bluetoothMain()
            }
            else -> {
                requestPermissionLauncherConnect.launch(
                    Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }

    private fun bluetoothMain() {
        enableBluetooth()
        //continues on enable bluetooth result
    }

    private fun bluetoothMainAfterEnable() {
        this.MAC = getDevice()
        println("MAC:::: ${this.MAC}")
        connect()

//        val resultIntent = Intent()
//        val bundle = Bundle()
//        bundle.putSerializable("lightInterface", this.lightInterface)
//        resultIntent.putExtras(bundle)
//
        setResult(
            RESULT_OK,
            Intent()
        )
        finish()
    }

    @SuppressLint("MissingPermission")
    fun enableBluetooth() {
        if (this.bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            // TODO handle
            System.err.println("No Bluetooth available")
            Toast.makeText(this, "Bluetooth adapter not available.", Toast.LENGTH_LONG)
                .show() // Replace context with your context instance.
            finish()
            return
        }


        if (!this.bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            bluetoothMainAfterEnable()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            println("Was able to turn on bluetooth.")
            bluetoothMainAfterEnable()
        }
        else if (requestCode == REQUEST_ENABLE_BT && resultCode != RESULT_OK) {
            Toast.makeText(this, "Needs active bluetooth to connect.", Toast.LENGTH_LONG)
                .show() // Replace context with your context instance.
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    fun getDevice(): String? {
        Log.d("My Bluetooth App", "Logging devices")
        val pairedDevices: Collection<BluetoothDevice> = this.lightInterface.getPairedDevicesList()
        for (device in pairedDevices) {
            Log.d("My Bluetooth App", "Device name: " + device.name)
            Log.d("My Bluetooth App", "Device MAC Address: " + device.address)
        }
        val filteredDevice = pairedDevices.filter{ it.name == "HC-06"}
        if (filteredDevice.isEmpty()) {
            Toast.makeText(this, "Couldn't find light device.", Toast.LENGTH_LONG)
                .show() // Replace context with your context instance.
            finish()
            return null
        }
        println(filteredDevice[0])
        val mac = filteredDevice[0]
        return mac.address
    }

    private fun connect() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_SCAN
            ) -> {
                lightInterface.connectDevice(this.MAC!!)
            }
            else -> {
                requestPermissionLauncherScan.launch(
                    Manifest.permission.BLUETOOTH_SCAN)
            }
        }
    }

    fun sendMessage(view: View) {
        this.lightInterface.send("Hello world! Button\n")
    }

}

