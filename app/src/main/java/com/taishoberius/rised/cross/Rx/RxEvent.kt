package com.taishoberius.rised.cross.Rx

import android.bluetooth.BluetoothDevice
import android.media.MediaMetadata
import com.taishoberius.rised.bluetooth.models.BluetoothState
import com.taishoberius.rised.main.services.model.Preferences
import com.taishoberius.rised.meteo.model.Forecast

class RxEvent {
    data class ForecastEvent(
        val success: Boolean,
        val forecast: Forecast?
    )

    class ForecastListEvent(
        val success: Boolean,
        val forecasts: List<Forecast>? = listOf()
    )

    data class BluetoothMediaEvent(
        val state: BluetoothState?,
        val media: MediaMetadata?
    )

    data class BluetoothProfileEvent(
        var device: BluetoothDevice?,
        val state: BluetoothState?
    )

    data class PreferenceEvent(
        var preference: Preferences
    )
}