package ami.proj.lightmediator

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.harrysoft.androidbluetoothserial.BluetoothManager
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.Serializable

class LightInterface(var context: Context): Serializable {
    private var bluetoothManager = BluetoothManager.getInstance()
    private var MAC: String? = null
    private var deviceInterface: SimpleBluetoothDeviceInterface? = null

    init {
        // Setup our BluetoothManager
        if (this.bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast.makeText(context, "Bluetooth not available.", Toast.LENGTH_LONG)
                .show() // Replace context with your context instance.
            finish()
        }
    }

    fun send(message: String) {
        this.deviceInterface?.sendMessage(message)
    }

    fun printMAC() {
        this.deviceInterface?.sendMessage("message")
        println(this.MAC)
    }

    fun isConnected(): Boolean {
        return this.deviceInterface != null
    }

    fun getPairedDevicesList(): Collection<BluetoothDevice> {
        val pairedDevices: Collection<BluetoothDevice> = bluetoothManager.getPairedDevicesList()
        return pairedDevices
    }

    @SuppressLint("CheckResult")
    fun connectDevice(mac: String) {
        this.MAC = mac
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
        this.deviceInterface?.sendMessage("{255,255,255}")
    }

    fun onMessageSent(message: String) {
        // We sent a message! Handle it here.
        Toast.makeText(context, "Sent a message! Message was: $message", Toast.LENGTH_LONG)
            .show() // Replace context with your context instance.
    }

    fun onMessageReceived(message: String) {
        Log.d("MessageReceived:" , message)
        // We received a message! Handle it here.
        Toast.makeText(context, "Received a message! Message was: $message", Toast.LENGTH_LONG)
            .show() // Replace context with your context instance.
    }

    fun onError(error: Throwable) {
        // Handle the error
        Log.e("Blue comm", "error: ", error)
    }

    fun finish() {
        // Please remember to destroy your instance after closing as it will no longer function!
        bluetoothManager.closeDevice(deviceInterface); // Close by interface instance

        // Disconnect all devices
        bluetoothManager.close();
    }
}