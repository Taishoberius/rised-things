package com.taishoberius.rised

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.things.pio.PeripheralManager
import com.taishoberius.rised.cross.com.ServiceManager
import com.taishoberius.rised.sensors.Arduino
import com.taishoberius.rised.sensors.MotionSensor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    private val TAG = "MainActivity"
    private lateinit var motionSensor: MotionSensor
    private lateinit var arduino: Arduino

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
        }
    }
}
