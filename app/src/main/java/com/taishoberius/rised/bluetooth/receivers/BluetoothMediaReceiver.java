package com.taishoberius.rised.bluetooth.receivers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.util.Log;

import com.taishoberius.rised.bluetooth.BluetoothActivity;

public class BluetoothMediaReceiver extends BroadcastReceiver {
    private BluetoothActivity activity;
    private static final String EXTRA_PLAYBACK = "android.bluetooth.avrcp-controller.profile.extra.PLAYBACK";
    private static final String EXTRA_MEDIADATA = "android.bluetooth.avrcp-controller.profile.extra.METADATA";

    public BluetoothMediaReceiver(BluetoothActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PlaybackState playbackState = (PlaybackState) intent.getExtras().get(EXTRA_PLAYBACK);
        MediaMetadata mediaMetadata = (MediaMetadata) intent.getExtras().get(EXTRA_MEDIADATA);
        if (playbackState != null) {
            if (playbackState.getState() == PlaybackState.STATE_PLAYING) {
                if (activity.bluetoothMediaDelegate != null)
                    activity.bluetoothMediaDelegate.onPlay();
            }
            if (playbackState.getState() == PlaybackState.STATE_PAUSED ||
                    playbackState.getState() == PlaybackState.STATE_STOPPED)
                if (activity.bluetoothMediaDelegate != null)
                    activity.bluetoothMediaDelegate.onMediaStop();
        }
        if (mediaMetadata != null) {
            if (activity.bluetoothMediaDelegate != null)
                activity.bluetoothMediaDelegate.onMediaDateChanged(mediaMetadata);
        }
    }
}
