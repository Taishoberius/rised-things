package com.taishoberius.rised

import android.bluetooth.BluetoothDevice
import android.media.MediaMetadata
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.things.pio.PeripheralManager
import com.google.firebase.iid.FirebaseInstanceId
import com.taishoberius.rised.bluetooth.BluetoothActivity
import com.taishoberius.rised.bluetooth.delegates.BluetoothMediaDelegate
import com.taishoberius.rised.bluetooth.delegates.BluetoothProfileDelegate
import com.taishoberius.rised.bluetooth.models.BluetoothState
import com.taishoberius.rised.bluetooth.models.MediaState
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import com.taishoberius.rised.sensors.Arduino
import com.taishoberius.rised.sensors.MotionSensor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BluetoothActivity(), BluetoothMediaDelegate, BluetoothProfileDelegate {
    private val TAG = "MainActivity"
    private lateinit var motionSensor: MotionSensor
    private lateinit var arduino: Arduino
    private var connectedDevice: BluetoothDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = PeripheralManager.getInstance()
        val portList: List<String> = manager.gpioList
        val uartList: List<String> = manager.uartDeviceList
        if (portList.isEmpty()) {
            Log.i(TAG, "No GPIO port available on this device.")
        } else {
            Log.i(TAG, "List of available ports: $portList")
        }
        if (uartList.isEmpty()) {
            Log.i(TAG, "No GPIO port available on this device.")
        } else {
            Log.i(TAG, "List of available ports: $uartList")
        }

        initView()
        initSensors()
        initListener()
        sleepMode(false)
        this.bluetoothMediaDelegate = this
        this.bluetoothProfileDelegate = this

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                Log.d("LETOKEN", token)
            })
    }

    private fun initView() {
    }

    private fun initSensors() {
        motionSensor = MotionSensor("BCM17")
        arduino = Arduino()
    }

    private fun initListener() {
        motionSensor.setOnValueChangedListener(object : MotionSensor.ValueChangedListener {
            override fun onValueChanged(newValue: Boolean) {
                sleepMode(!newValue)
            }

        })

        simulate_button.setOnClickListener {
            findAndConnectNearestDevice()
        }
    }

    private fun findAndConnectNearestDevice() {
        if (connectedDevice != null) return
        this.findAndConnectBondedDevice();
    }

    private fun sleepMode(sleep: Boolean) {
        if(sleep) {
            Log.i(TAG, "Enter sleep mode")
            sleep_mode.visibility = View.VISIBLE
            arduino.setOnValueChangedListener(null)
        } else {
            Log.i(TAG, "Exit sleep mode")
            sleep_mode.visibility = View.GONE
            arduino.setOnValueChangedListener(object : Arduino.ValueChangedListener {
                override fun onTemperatureChangedChanged(newValue: Float) {
                    txv_temperature.text = getString(R.string.current_temperature, newValue)
                }

                override fun onHumidityChanged(newValue: Float) {
                    txv_humidity.text = getString(R.string.current_humidity, newValue.toInt())
                }

            })

            connectedDevice?.run {
                RxBus.publish(RxEvent.BluetoothProfileEvent(this, BluetoothState.CONNECTED))
            }
        }
    }

    override fun onPlay() {
        RxBus.publish(RxEvent.BluetoothMediaEvent(BluetoothState.MEDIA_PLAYING, null))
    }

    override fun onMediaStop() {
        RxBus.publish(RxEvent.BluetoothMediaEvent(BluetoothState.MEDIA_PAUSED, null))
    }

    override fun onMediaDateChanged(mediaMetadata: MediaMetadata?) {
        RxBus.publish(RxEvent.BluetoothMediaEvent(null, mediaMetadata))
    }

    override fun onConnected(device: BluetoothDevice?) {
        this.connectedDevice = device
        RxBus.publish(RxEvent.BluetoothProfileEvent(device, BluetoothState.CONNECTED))
    }

    override fun onDisconnected(device: BluetoothDevice?) {
        this.connectedDevice = null
        RxBus.publish(RxEvent.BluetoothProfileEvent(device, BluetoothState.DISCONNECTED))
    }

    override fun onStateChange(newState: Int) {
        if (newState == 0)
            onDisconnected(null)
    }
}
