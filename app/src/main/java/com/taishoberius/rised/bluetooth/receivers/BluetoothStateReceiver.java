package com.taishoberius.rised.bluetooth.receivers;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.taishoberius.rised.bluetooth.BluetoothActivity;
import com.taishoberius.rised.bluetooth.models.A2dpSinkHelper;

import java.util.Objects;

public class BluetoothStateReceiver extends BroadcastReceiver {
    private BluetoothActivity activity;

    public BluetoothStateReceiver(BluetoothActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(A2dpSinkHelper.ACTION_CONNECTION_STATE_CHANGED)) {
            int oldState = A2dpSinkHelper.getPreviousProfileState(intent);
            int newState = A2dpSinkHelper.getCurrentProfileState(intent);
            BluetoothDevice device = A2dpSinkHelper.getDevice(intent);
            if (activity.bluetoothProfileDelegate != null)
                this.activity.bluetoothProfileDelegate.onStateChange(newState);
            if (device != null) {
                String deviceName = Objects.toString(device.getName(), "a device");
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    if (activity.bluetoothProfileDelegate != null)
                        activity.bluetoothProfileDelegate.onConnected(device);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if (activity.bluetoothProfileDelegate != null)
                        activity.bluetoothProfileDelegate.onDisconnected(device);
                }
            }
        }
    }
}
