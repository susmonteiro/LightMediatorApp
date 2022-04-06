package ami.proj.lightmediator

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.harrysoft.androidbluetoothserial.BluetoothManager
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class BluetoothActivity : AppCompatActivity() {

    private var bluetoothManager = BluetoothManager.getInstance()
    private var MAC: String? = null
    private var deviceInterface: SimpleBluetoothDeviceInterface? = null


    private val requestPermissionLauncherConnect =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                println("Permission granted!")
                this.MAC = getDevice()
            } else {
                println("Permission not granted...")
                // TODO send to page
//                    val intent = Intent(this, RequestPermissionActivity::class.java)
//                    startActivity(intent)
            }
        }

    private val requestPermissionLauncherScan =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                println("Permission granted!")
            } else {
                println("Permission not granted...")
                // TODO send to page
//                    val intent = Intent(this, RequestPermissionActivity::class.java)
//                    startActivity(intent)
            }
        }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        // Setup our BluetoothManager
        // Setup our BluetoothManager
        if (this.bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast.makeText(this, "Bluetooth not available.", Toast.LENGTH_LONG)
                .show() // Replace context with your context instance.
            finish()
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncherConnect.launch(
                Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            this.MAC = getDevice()
        }

        println("MAC:::: ${this.MAC}")
        if (this.MAC == null) throw Error("Unable to get device")
        else
            connectDevice(this.MAC!!)

    }

    @SuppressLint("MissingPermission")
    fun getDevice(): String {
        Log.d("My Bluetooth App", "Logging devices")
        val pairedDevices: Collection<BluetoothDevice> = bluetoothManager.getPairedDevicesList()
        for (device in pairedDevices) {
            Log.d("My Bluetooth App", "Device name: " + device.name)
            Log.d("My Bluetooth App", "Device MAC Address: " + device.address)
        }
        val filteredDevice = pairedDevices.filter{ it.name == "HC-06"}
        println(filteredDevice[0])
        val mac = filteredDevice[0]
        return mac.address
    }

    private fun connectDevice(mac: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncherScan.launch(
                Manifest.permission.BLUETOOTH_SCAN)
        }

        this.bluetoothManager.openSerialDevice(mac)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onConnected, this::onError)
    }

    private fun onConnected(connectedDevice: BluetoothSerialDevice) {
        // You are now connected to this device!
        // Here you may want to retain an instance to your device:
        this.deviceInterface = connectedDevice.toSimpleDeviceInterface()

        // Listen to bluetooth events
        this.deviceInterface?.setListeners(null, this::onMessageSent, this::onError)

        // Let's send a message:
        this.deviceInterface?.sendMessage("Hello world!")
    }

    private fun onMessageSent(message: String) {
        // We sent a message! Handle it here.
        Toast.makeText(this, "Sent a message! Message was: $message", Toast.LENGTH_LONG)
            .show() // Replace context with your context instance.
    }

    private fun onMessageReceived(message: String) {
        Log.d("MessageReceived:" , message)
        // We received a message! Handle it here.
        Toast.makeText(this, "Received a message! Message was: $message", Toast.LENGTH_LONG)
            .show() // Replace context with your context instance.
    }

    private fun onError(error: Throwable) {
        // Handle the error
        Log.e("Blue comm", "error: ", error)
    }


    override fun finish() {
        super.finish()

        // Please remember to destroy your instance after closing as it will no longer function!
        bluetoothManager.closeDevice(deviceInterface); // Close by interface instance

        // Disconnect all devices
        bluetoothManager.close();
    }

    fun sendMessage(view: View) {
        this.deviceInterface?.sendMessage("Hello world! Button\n")
    }
}

