package com.taishoberius.rised.bluetooth.delegates;
import android.bluetooth.BluetoothDevice;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;

public interface BluetoothPlaybackDelegate {
    void onPlaybackStateChange(int newState);
    void onStartPlaying(BluetoothDevice device);
    void onStopPlaying(BluetoothDevice device);
}
