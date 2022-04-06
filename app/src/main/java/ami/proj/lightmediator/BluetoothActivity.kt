package ami.proj.lightmediator

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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



    }

    @SuppressLint("MissingPermission")
    fun enableBluetooth() {
        var REQUEST_ENABLE_BT = 2; // Returned as requestCode
        if (this.bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)


            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            if (REQUEST_ENABLE_BT == RESULT_CANCELED) {
                // TODO handle not on
                System.err.println("Not able to turn on bluetooth.")
            }
            println(REQUEST_ENABLE_BT)
        }
    }

}