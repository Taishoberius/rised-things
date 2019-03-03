package com.taishoberius.rised

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.PeripheralManager
import com.taishoberius.rised.sensors.MotionSensor

class MainActivity : Activity() {

    private val TAG = "MainActivity"
    private lateinit var motionSensor: MotionSensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = PeripheralManager.getInstance()
        val portList: List<String> = manager.gpioList
        if (portList.isEmpty()) {
            Log.i(TAG, "No GPIO port available on this device.")
        } else {
            Log.i(TAG, "List of available ports: $portList")
        }
        initView()
        initListener()
    }

    fun initView() {
        val motionSensor = MotionSensor("BCM17")
    }

    fun initListener() {
        motionSensor.setOnValueChangedListener(object : MotionSensor.ValueChangedListener {
            override fun onValueChanged(newValue: Boolean) {
                if(newValue) {
                    setContentView(R.layout.activity_main)
                } else {
                    setContentView(R.layout.activity_empty)
                }
            }

        })
    }
}
