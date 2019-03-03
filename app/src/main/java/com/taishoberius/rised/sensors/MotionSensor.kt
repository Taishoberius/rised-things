package com.taishoberius.rised.sensors

import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class MotionSensor() {
    private val TAG = "MotionSensor"

    private var motionSensor: Gpio? = null
    private lateinit var listener: ValueChangedListener
    private var isWaiting = false

    private val gpioCallback = object : GpioCallback {
        override fun onGpioEdge(gpio: Gpio): Boolean {
            Log.d(TAG, "${gpio.value}, $isWaiting")
            if (!isWaiting) {
                if (gpio.value) {
                    listener.onValueChanged(true)
                } else {
                    waitForSomeOne(gpio)
                }
            }

            // Continue listening for more interrupts
            return true
        }

        override fun onGpioError(gpio: Gpio, error: Int) {
            Log.w(TAG, "$gpio: Error event $error")
        }
    }

    constructor(bcm: String) : this() {
        motionSensor = try {
            PeripheralManager.getInstance()
                .openGpio(bcm)
        } catch (e: IOException) {
            Log.w(TAG, "Unable to access GPIO", e)
            null
        }

        motionSensor?.apply {
            // Initialize the pin as an input
            setDirection(Gpio.DIRECTION_IN)
            // Low voltage is considered active
            setActiveType(Gpio.ACTIVE_HIGH)

            // Register for all state changes
            setEdgeTriggerType(Gpio.EDGE_BOTH)
            registerGpioCallback(gpioCallback)
        }
    }

    private fun waitForSomeOne(gpio: Gpio) {
        isWaiting = true
        GlobalScope.launch(context = Dispatchers.Main) {
            for(i in 0..20) {
                delay(500)
                if (!gpio.value) {
                    listener.onValueChanged(false)
                    break
                }
            }
            isWaiting = false
        }
    }

    fun setOnValueChangedListener(listener: ValueChangedListener) {
        this.listener = listener
    }

    interface ValueChangedListener {
        fun onValueChanged(newValue: Boolean)
    }

}