package com.example.meteo_station

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class BleScanner(context: Context) {

    private val tag = "BleScanner"

    companion object {
        const val NRF_NAME = "Meteo_Station_Beacon"
    }

    private val leScanner =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
            .adapter.bluetoothLeScanner

    var onPacketReceived: ((BeaconPacket) -> Unit)? = null

    private val callback = object : ScanCallback() {

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val record = result.scanRecord ?: return

            // Confirm it's our beacon by presence of company ID key
            val isOurBeacon = record.getManufacturerSpecificData(BeaconPacket.COMPANY_ID) != null
            if (!isOurBeacon) return

            // Custom 0xA5 environment data
            val envRaw: ByteArray? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                parseCustomAdType(record.bytes, 0xA5.toByte())
            } else null

            val packet = BeaconPacket(
                deviceAddress = result.device.address,
                rssi = result.rssi,
                envRaw = envRaw,
            )
            val varr = packet.getTemperature().toString()
            val var1 = packet.getPressure().toString()
            val uvIndex = packet.getUVIndex().toString()
            val co2 = packet.getCO2().toString()
            val compound = packet.getVolatileCompound().toString()
            Log.d(tag,"$varr C, $var1 Pa, $uvIndex Index, CO2: $co2, Compound Organic: $compound")
            Log.d(tag, packet.toString())

            onPacketReceived?.invoke(packet)
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(tag, "Scan failed: error $errorCode")
        }
    }

    private fun parseCustomAdType(raw: ByteArray?, targetType: Byte): ByteArray? {
        if (raw == null) return null
        var i = 0
        while (i < raw.size) {
            val len = raw[i].toInt() and 0xFF
            if (len == 0) break
            if (i + 1 + len > raw.size) break
            if (raw[i + 1] == targetType) {
                return raw.copyOfRange(i + 2, i + 1 + len)
            }
            i += 1 + len
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun start() {
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(0)
            .setLegacy(false)
            .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
            .build()

        val filters = listOf(
            ScanFilter.Builder()
                .setDeviceName(NRF_NAME)
                .build()
        )

        leScanner.startScan(filters, settings, callback)
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        leScanner.stopScan(callback)
        Log.i(tag, "Scan stopped")
    }
}