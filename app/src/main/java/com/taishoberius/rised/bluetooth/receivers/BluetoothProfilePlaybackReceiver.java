package com.taishoberius.rised.bluetooth.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.taishoberius.rised.bluetooth.BluetoothActivity;
import com.taishoberius.rised.bluetooth.models.A2dpSinkHelper;

public class BluetoothProfilePlaybackReceiver extends BroadcastReceiver {
    private BluetoothActivity activity;

    public BluetoothProfilePlaybackReceiver(BluetoothActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(A2dpSinkHelper.ACTION_PLAYING_STATE_CHANGED)) {
            int oldState = A2dpSinkHelper.getPreviousProfileState(intent);
            int newState = A2dpSinkHelper.getCurrentProfileState(intent);
            BluetoothDevice device = A2dpSinkHelper.getDevice(intent);
            if (activity.bluetoothAudioDelegate != null)
                this.activity.bluetoothAudioDelegate.onPlaybackStateChange(newState);
            if (device != null) {
                if (newState == A2dpSinkHelper.STATE_PLAYING) {
                    if (activity.bluetoothAudioDelegate != null)
                        this.activity.bluetoothAudioDelegate.onStartPlaying(device);
                } else if (newState == A2dpSinkHelper.STATE_NOT_PLAYING) {
                    if (activity.bluetoothAudioDelegate != null)
                        this.activity.bluetoothAudioDelegate.onStopPlaying(device);
                }
            }
        }
    }
}
