package com.taishoberius.rised.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertiseSettings.Builder;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.things.bluetooth.BluetoothProfileManager;
import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.taishoberius.rised.bluetooth.delegates.BluetoothAdapterDelegate;
import com.taishoberius.rised.bluetooth.delegates.BluetoothDeviceDelegate;
import com.taishoberius.rised.bluetooth.delegates.BluetoothMediaDelegate;
import com.taishoberius.rised.bluetooth.delegates.BluetoothPlaybackDelegate;
import com.taishoberius.rised.bluetooth.delegates.BluetoothProfileDelegate;
import com.taishoberius.rised.bluetooth.models.A2dpSinkHelper;
import com.taishoberius.rised.bluetooth.models.BoardDefaults;
import com.taishoberius.rised.bluetooth.receivers.BluetoothDeviceReceiver;
import com.taishoberius.rised.bluetooth.receivers.BluetoothMediaReceiver;
import com.taishoberius.rised.bluetooth.receivers.BluetoothProfilePlaybackReceiver;
import com.taishoberius.rised.bluetooth.receivers.BluetoothStateReceiver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class BluetoothActivity extends AppCompatActivity {
    public BluetoothAdapterDelegate bluetoothAdapterDelegate;
    public BluetoothProfileDelegate bluetoothProfileDelegate;
    public BluetoothPlaybackDelegate bluetoothAudioDelegate;
    public BluetoothMediaDelegate bluetoothMediaDelegate;
    public BluetoothDeviceDelegate bluetoothDeviceDelegate;

    private UUID SERVICE_UUID = UUID.fromString("795090c7-420d-4048-a24e-18e60180e23c");


    private static final String TAG = "A2dpSinkActivity";

    private static final String TRACK_EVENT_INTENT = "android.bluetooth.avrcp-controller.profile.action.TRACK_EVENT";

    private static final String ADAPTER_FRIENDLY_NAME = "Erised";
    private static final int DISCOVERABLE_TIMEOUT_MS = 300;
    private static final int REQUEST_CODE_ENABLE_DISCOVERABLE = 100;


    private static final String UTTERANCE_ID =
            "com.example.androidthings.bluetooth.audio.UTTERANCE_ID";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothProfile mA2DPSinkProxy;

    private ButtonInputDriver mPairingButtonDriver;
    private ButtonInputDriver mDisconnectAllButtonDriver;

    /**
     * Handle an intent that is broadcast by the Bluetooth adapter whenever it changes its
     * state (after calling enable(), for example).
     * Action is {@link BluetoothAdapter#ACTION_STATE_CHANGED} and extras describe the old
     * and the new states. You can use this intent to indicate that the device is ready to go.
     */
    private final BroadcastReceiver mAdapterStateChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int oldState = A2dpSinkHelper.getPreviousAdapterState(intent);
            int newState = A2dpSinkHelper.getCurrentAdapterState(intent);
            Log.d(TAG, "Bluetooth Adapter changing state from " + oldState + " to " + newState);

            if (newState == BluetoothAdapter.STATE_ON) {
                Log.i(TAG, "Bluetooth Adapter is ready");
                initA2DPSink();
            }
        }
    };

    /**
     * Handle an intent that is broadcast by the Bluetooth A2DP sink profile whenever a device
     * connects or disconnects to it.
     * Action is {@link A2dpSinkHelper#ACTION_CONNECTION_STATE_CHANGED} and
     * extras describe the old and the new connection states. You can use it to indicate that
     * there's a device connected.
     */
    private final BroadcastReceiver mSinkProfileStateChangeReceiver = new BluetoothStateReceiver(this);

    /**
     * Handle an intent that is broadcast by the Bluetooth A2DP sink profile whenever a device
     * starts or stops playing through the A2DP sink.
     * Action is {@link A2dpSinkHelper#ACTION_PLAYING_STATE_CHANGED} and
     * extras describe the old and the new playback states. You can use it to indicate that
     * there's something playing. You don't need to handle the stream playback by yourself.
     */

    private final BroadcastReceiver mSinkProfilePlaybackChangeReceiver = new BluetoothProfilePlaybackReceiver(this);
    private final BroadcastReceiver bluetoothMediaReceiver = new BluetoothMediaReceiver(this);
    private BroadcastReceiver bluetoothDeviceReceiver = new BluetoothDeviceReceiver(this);
    private Timer controlTimer;
    private BluetoothSocket deviceSocket;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    public BluetoothActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setupBluetooth();
    }

    public void setupBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            if (this.bluetoothAdapterDelegate!= null) {
                this.bluetoothAdapterDelegate.onNotFound();
            }
            return;
        }

        registerReceiver(mAdapterStateChangeReceiver, new IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(mSinkProfileStateChangeReceiver, new IntentFilter(
                A2dpSinkHelper.ACTION_CONNECTION_STATE_CHANGED));
        registerReceiver(mSinkProfilePlaybackChangeReceiver, new IntentFilter(
                A2dpSinkHelper.ACTION_PLAYING_STATE_CHANGED));
        registerReceiver(this.bluetoothMediaReceiver, new IntentFilter(TRACK_EVENT_INTENT));
        registerReceiver(this.bluetoothDeviceReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));


        if (mBluetoothAdapter.isEnabled()) {
            if (this.bluetoothAdapterDelegate != null) {
                this.bluetoothAdapterDelegate.onEnabled();
            }
            initA2DPSink();
        } else {
            if (this.bluetoothAdapterDelegate != null) {
                this.bluetoothAdapterDelegate.onEnabling();
            }
            mBluetoothAdapter.enable();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_P:
                // Enable Pairing mode (discoverable)
                enableDiscoverable();
                return true;
            case KeyEvent.KEYCODE_D:
                // Disconnect any currently connected devices
                disconnectConnectedDevices();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        try {
            if (mPairingButtonDriver != null) mPairingButtonDriver.close();
        } catch (IOException e) { /* close quietly */}
        try {
            if (mDisconnectAllButtonDriver != null) mDisconnectAllButtonDriver.close();
        } catch (IOException e) { /* close quietly */}

        unregisterReceiver(mAdapterStateChangeReceiver);
        unregisterReceiver(mSinkProfileStateChangeReceiver);
        unregisterReceiver(mSinkProfilePlaybackChangeReceiver);
        unregisterReceiver(this.bluetoothMediaReceiver);
        unregisterReceiver(this.bluetoothDeviceReceiver);

        if (mA2DPSinkProxy != null) {
            mBluetoothAdapter.closeProfileProxy(A2dpSinkHelper.A2DP_SINK_PROFILE,
                    mA2DPSinkProxy);
        }

        // we intentionally leave the Bluetooth adapter enabled, so that other samples can use it
        // without having to initialize it.
    }

    private void setupBTProfiles() {
        BluetoothProfileManager bluetoothProfileManager = BluetoothProfileManager.getInstance();
        List<Integer> enabledProfiles = bluetoothProfileManager.getEnabledProfiles();
        if (!enabledProfiles.contains(A2dpSinkHelper.A2DP_SINK_PROFILE)) {
            Log.d(TAG, "Enabling A2dp sink mode.");
            List<Integer> toDisable = Arrays.asList(BluetoothProfile.A2DP);
            List<Integer> toEnable = Arrays.asList(
                    A2dpSinkHelper.A2DP_SINK_PROFILE,
                    A2dpSinkHelper.AVRCP_CONTROLLER_PROFILE);
            bluetoothProfileManager.enableAndDisableProfiles(toEnable, toDisable);
        } else {
            Log.d(TAG, "A2dp sink profile is enabled.");
        }
    }

    /**
     * Initiate the A2DP sink.
     */
    private void initA2DPSink() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            if (bluetoothAdapterDelegate != null) {
                this.bluetoothAdapterDelegate.onNotAvailable();
            }
            return;
        }
        setupBTProfiles();
        Log.d(TAG, "Set up Bluetooth Adapter name and profile");
        mBluetoothAdapter.setName(ADAPTER_FRIENDLY_NAME);
        startAdvertising();
        mBluetoothAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                mA2DPSinkProxy = proxy;
                enableDiscoverable();
            }
            @Override
            public void onServiceDisconnected(int profile) {
            }
        }, A2dpSinkHelper.A2DP_SINK_PROFILE);

        //configureButton();
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: " + errorCode);
        }
    };
    /**
     * Enable the current {@link BluetoothAdapter} to be discovered (available for pairing) for
     * the next {@link #DISCOVERABLE_TIMEOUT_MS} ms.
     */
    private void enableDiscoverable() {
        Log.d(TAG, "Registering for discovery.");
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                DISCOVERABLE_TIMEOUT_MS);
        startActivityForResult(discoverableIntent, REQUEST_CODE_ENABLE_DISCOVERABLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE_DISCOVERABLE) {
            Log.d(TAG, "Enable discoverable returned with result " + resultCode);

            // ResultCode, as described in BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE, is either
            // RESULT_CANCELED or the number of milliseconds that the device will stay in
            // discoverable mode. In a regular Android device, the user will see a popup requesting
            // authorization, and if they cancel, RESULT_CANCELED is returned. In Android Things,
            // on the other hand, the authorization for pairing is always given without user
            // interference, so RESULT_CANCELED should never be returned.
            if (resultCode == RESULT_CANCELED) {
                if (bluetoothAdapterDelegate != null) {
                    this.bluetoothAdapterDelegate.onDiscoverableCanceled();
                }
                return;
            }
            if (bluetoothAdapterDelegate != null) {
                this.bluetoothAdapterDelegate.onIsDiscoverable();
            }
        }
    }

    private void disconnectConnectedDevices() {
        if (mA2DPSinkProxy == null || mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return;
        }
        for (BluetoothDevice device: mA2DPSinkProxy.getConnectedDevices()) {
            A2dpSinkHelper.disconnect(mA2DPSinkProxy, device);
        }
    }

    private void configureButton() {
        try {
            mPairingButtonDriver = new ButtonInputDriver(BoardDefaults.getGPIOForPairing(),
                    Button.LogicState.PRESSED_WHEN_LOW, KeyEvent.KEYCODE_P);
            mPairingButtonDriver.register();
            mDisconnectAllButtonDriver = new ButtonInputDriver(
                    BoardDefaults.getGPIOForDisconnectAllBTDevices(),
                    Button.LogicState.PRESSED_WHEN_LOW, KeyEvent.KEYCODE_D);
            mDisconnectAllButtonDriver.register();
        } catch (IOException e) {
            Log.w(TAG, "Could not register GPIO button drivers. Use keyboard events to trigger " +
                    "the functions instead", e);
        } catch (Exception ex) {
            System.out.println("ignored exception");
        }
    }

    public  void findAndConnectBondedDevice() {
        enableDiscoverable();
        startAdvertising();
        this.mBluetoothAdapter.cancelDiscovery();
        final ArrayList<BluetoothDevice> devices = new ArrayList<>(this.mBluetoothAdapter.getBondedDevices());
        for (BluetoothDevice device: devices) {
            if (connect(device)) {
                if (this.bluetoothProfileDelegate != null)
                    this.bluetoothProfileDelegate.onConnected(device);
                return;
            }
        }
    }

    private void startAdvertising() {
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

// Defines which service to advertise.
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(SERVICE_UUID))
                .build();

// Starts advertising.
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
    }

    private Boolean connect(BluetoothDevice device) {
        final UUID uuid = device.getUuids()[0].getUuid();
        final BluetoothDevice remote = this.mBluetoothAdapter.getRemoteDevice(device.getAddress());

        try {
            deviceSocket = (BluetoothSocket)remote.getClass().getMethod("createRfcommSocket", new Class[]{Integer.TYPE}).invoke(device, new Object[]{Integer.valueOf(1)});

            deviceSocket.connect();
        } catch (IOException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return false;
        }

        if (deviceSocket.isConnected()) {
            launchSocketConnectionControl();
        }

        return true;
    }

    private void launchSocketConnectionControl() {
        controlTimer = new Timer();
        controlTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    deviceSocket.getInputStream().read();
                } catch (IOException e) {
                    if (bluetoothProfileDelegate != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bluetoothProfileDelegate.onDisconnected(null);
                            }
                        });
                    }
                    deviceSocket = null;
                    controlTimer.cancel();
                    controlTimer.purge();
                }

            }
        }, 1000, 3000);
    }
}
