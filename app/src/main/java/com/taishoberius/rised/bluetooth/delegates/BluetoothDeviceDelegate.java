package com.taishoberius.rised.bluetooth.delegates;

import android.bluetooth.BluetoothDevice;

public interface BluetoothDeviceDelegate {
    void onDeviceDiscovered(BluetoothDevice device);
}
