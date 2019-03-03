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

    constructor(bcm: String) : this() {
        motionSensor = try {
            PeripheralManager.getInstance()
                .openGpio(bcm)
        } catch (e: IOException) {
            Log.w(TAG, "Unable to access GPIO", e)
            null
        }

        motionSensor?.apply {
            setDirection(Gpio.DIRECTION_IN)
            setActiveType(Gpio.ACTIVE_HIGH)
            setEdgeTriggerType(Gpio.EDGE_BOTH)
            registerGpioCallback(object : GpioCallback {
                override fun onGpioEdge(gpio: Gpio): Boolean {
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
            })
        }
    }

    private fun waitForSomeOne(gpio: Gpio) {
        isWaiting = true
        var hasDetectedSomething =false
        GlobalScope.launch(context = Dispatchers.Main) {
            //check every 500ms for 10sec if there is something
            for(i in 0..20) {
                delay(500)
                if (gpio.value) {
                    hasDetectedSomething = true
                    break
                }
            }
            isWaiting = false
            //No one was detected, so say it
            if (!hasDetectedSomething) {
                listener.onValueChanged(false)
            }
        }
    }

    fun setOnValueChangedListener(listener: ValueChangedListener) {
        this.listener = listener
    }

    interface ValueChangedListener {
        fun onValueChanged(newValue: Boolean)
    }

}