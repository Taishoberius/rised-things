package com.taishoberius.rised.bluetooth.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.taishoberius.rised.bluetooth.BluetoothActivity;

public class BluetoothDeviceReceiver extends BroadcastReceiver {
    private BluetoothActivity activity;

    public BluetoothDeviceReceiver(BluetoothActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (activity.bluetoothDeviceDelegate != null)
                activity.bluetoothDeviceDelegate.onDeviceDiscovered(device);
        }
    }
}
