package com.example.meteo_station

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.meteo_station.ui.theme.Meteo_StationTheme


class MainActivity : ComponentActivity() {
    private lateinit var scanner: BleScanner

    // ── Stored data ───────────────────────────────────────────────────────────
    var lastPacket: BeaconPacket? = null
    var packetCount: Int = 0
    val packetHistory: ArrayDeque<BeaconPacket> = ArrayDeque(MAX_HISTORY)

    companion object {
        private const val TAG = "MainActivity"
        private const val MAX_HISTORY = 100
        private const val PERMISSION_REQUEST_CODE = 1
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Meteo_StationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainComposer()
                }
            }
        }
        //scanner = BleScanner(this)
        //scanner.onPacketReceived = ::onPacketReceived

        //if (hasPermissions()) startScanning() else requestPermissions()
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

    private fun onPacketReceived(packet: BeaconPacket) {
        lastPacket = packet
        packetCount++

        if (packetHistory.size >= MAX_HISTORY) packetHistory.removeFirst()
        packetHistory.addLast(packet)

        Log.d(TAG, "Packet #$packetCount — $packet")
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