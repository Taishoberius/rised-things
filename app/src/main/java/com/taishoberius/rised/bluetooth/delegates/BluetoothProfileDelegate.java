package com.taishoberius.rised.bluetooth.delegates;
import android.bluetooth.BluetoothDevice;

public interface BluetoothProfileDelegate {
    void onConnected(BluetoothDevice device);
    void onDisconnected(BluetoothDevice device);

    void onStateChange(int newState);
}
