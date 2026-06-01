package com.example.meteo_station

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.meteo_station.ui.theme.Meteo_StationTheme


class MainActivity : ComponentActivity() {
    private lateinit var scanner: BleScanner

    companion object {
        private const val TAG = "MainActivity"
        private const val PERMISSION_REQUEST_CODE = 1
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        scanner = BleScanner(this)

        setContent {
            val meteoViewModel: MeteoViewModel = viewModel()
            scanner.onPacketReceived = meteoViewModel::onPacketReceived

            Meteo_StationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainComposer(meteoViewModel)
                }
            }
        }

        if (hasPermissions()) checkBluetoothAndScan() else requestPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        scanner.stop()
    }

    // ── Scanning ──────────────────────────────────────────────────────────────

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startScanning() {
        scanner.start()
        Log.i(TAG, "Scanning started")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            startScanning()
        } else {
            // User declined — show a message
            Toast.makeText(this, "Bluetooth is required", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkBluetoothAndScan() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        when {
            bluetoothAdapter == null -> {
                // Device doesn't support Bluetooth
                Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show()
            }
            !bluetoothAdapter.isEnabled -> {
                // Prompt user to enable Bluetooth
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
            }
            else -> startScanning()
        }
    }

    // ── Permissions ───────────────────────────────────────────────────────────

    private fun hasPermissions(): Boolean {
        val result = requiredPermissions().all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        Log.d(TAG, "Has permissions: $result")
        requiredPermissions().forEach {
            val state = ContextCompat.checkSelfPermission(this, it)
            Log.d(TAG, "  $it → ${if (state == PackageManager.PERMISSION_GRANTED) "GRANTED" else "DENIED"}")
        }
        return result
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, requiredPermissions(), PERMISSION_REQUEST_CODE)
    }

    private fun requiredPermissions(): Array<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
        else
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            startScanning()
        } else {
            Log.e(TAG, "BLE permissions denied — cannot scan")
        }
    }
}