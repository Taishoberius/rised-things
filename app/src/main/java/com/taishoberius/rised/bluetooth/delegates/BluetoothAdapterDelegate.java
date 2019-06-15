package com.taishoberius.rised.bluetooth.delegates;
import android.content.Intent;

import com.taishoberius.rised.bluetooth.models.BoardDefaults;

/**
 * Sample usage of the A2DP sink bluetooth profile. At startup, this activity sets the Bluetooth
 * adapter in pairing mode for {@link #DISCOVERABLE_TIMEOUT_MS} ms.
 *
 * To re-enable pairing mode, press "p" on an attached keyboard, use "adb shell input keyevent 44"
 * or press a button attached to the GPIO pin returned by {@link BoardDefaults#getGPIOForPairing()}
 *
 *  To forcefully disconnect any connected A2DP device, press "d" on an attached keyboard, use
 *  "adb shell input keyevent 32" or press a button attached to the GPIO pin
 *  returned by {@link BoardDefaults#getGPIOForDisconnectAllBTDevices()}

 *
 * NOTE: While in pairing mode, pairing requests are auto-accepted - at this moment there's no
 * way to block specific pairing attempts while in pairing mode. This is known limitation that is
 * being worked on.
 *
 */
public interface BluetoothAdapterDelegate {
    void onNotFound();
    void onEnabled();
    void onEnabling();
    void onNotAvailable();
    void onDiscoverableCanceled();
    void onIsDiscoverable();

    void onIntent(Intent intent);
}
