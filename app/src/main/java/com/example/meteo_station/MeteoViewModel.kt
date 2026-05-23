package com.example.meteo_station


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MeteoViewModel : ViewModel() {
    var lastPacket: BeaconPacket? by mutableStateOf(null)
        private set

    fun onPacketReceived(packet: BeaconPacket) {
        lastPacket = packet
    }
}