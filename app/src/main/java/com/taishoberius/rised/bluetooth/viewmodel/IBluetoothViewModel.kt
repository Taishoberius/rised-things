package com.taishoberius.rised.bluetooth.viewmodel

import androidx.lifecycle.MutableLiveData
import com.taishoberius.rised.bluetooth.models.BluetoothInfo

public interface IBluetoothViewModel {
    fun getMediaInfoLiveData(): MutableLiveData<BluetoothInfo>
    fun onCardViewDetached()
}

