package com.example.meteo_station

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class BeaconPacket(
    val deviceAddress: String,
    val rssi: Int,
    val envRaw: ByteArray?,  // payload from custom 0xA5 AD type (16 bytes)
) {
    companion object {
        const val COMPANY_ID = 0x0059
    }

    // ── Environment data (AD type 0xA5) ─────────────────────────────────────


    fun getTemperature() : Float{
        return ByteBuffer.wrap(envRaw).order(ByteOrder.LITTLE_ENDIAN).getFloat()
    }

    fun getPressure() : Float{
        return ByteBuffer.wrap(envRaw).order(ByteOrder.LITTLE_ENDIAN).getFloat(4)
    }

    fun getUVIndex(): Byte?{
        return envRaw?.get(8)
    }

    fun getCO2() : Int{
        return ByteBuffer.wrap(envRaw).order(ByteOrder.BIG_ENDIAN).getShort(9).toInt() and 0xFFFF
    }

    fun getVolatileCompound() : Int{
        return ByteBuffer.wrap(envRaw).order(ByteOrder.BIG_ENDIAN).getShort(11).toInt() and 0xFFFF
    }
    // ── Debug ────────────────────────────────────────────────────────────────
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BeaconPacket

        if (rssi != other.rssi) return false
        if (deviceAddress != other.deviceAddress) return false
        if (!envRaw.contentEquals(other.envRaw)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rssi
        result = 31 * result + deviceAddress.hashCode()
        result = 31 * result + (envRaw?.contentHashCode() ?: 0)
        return result
    }

}