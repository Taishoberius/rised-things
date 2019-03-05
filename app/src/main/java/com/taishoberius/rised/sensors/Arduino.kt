package com.taishoberius.rised.sensors

import android.util.Log
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.UartDevice
import com.google.android.things.pio.UartDeviceCallback
import java.io.IOException
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.util.jar.JarEntry

class Arduino(uartPin: String = "UART0") {
    private val TAG = "Arduino"

    private val TEMPERATURE_INDICATOR = 't'
    private val HUMIDITY_INDICATOR = 'h'
    private var mDevice: UartDevice? = null
    private var listener: ValueChangedListener? = null

    init {
        mDevice = try {
            PeripheralManager.getInstance()
                .openUartDevice(uartPin)
        } catch (e: IOException) {
            Log.w(TAG, "Unable to access UART device", e)
            null
        }
        mDevice?.apply {
            // Configure the UART port
            setBaudrate(9600)
            setDataSize(8)
            setParity(UartDevice.PARITY_NONE)
            setStopBits(1)
        }
        mDevice?.registerUartDeviceCallback(object : UartDeviceCallback {
            override fun onUartDeviceDataAvailable(uart: UartDevice): Boolean {
                // Read available data from the UART device
                try {
                    var data = readUartBuffer(uart).trim()
                    try {
                        Log.v(TAG, "Received '${data.substring(0, data.indexOf('\r'))}' from Arduino")
                    } catch (sioobe: StringIndexOutOfBoundsException) {
                        Log.v(TAG, "Received data from Arduino")
                    }
                    when (data[0]) {
                        TEMPERATURE_INDICATOR -> {
                            data.replace(" ", "")
                            data = data.substring(1)
                            try {
                                val temperature = data.toFloat()
                                listener?.onTemperatureChangedChanged(temperature)
                            } catch (nfe: NumberFormatException) {

                            }
                        }
                        HUMIDITY_INDICATOR -> {
                            data.replace(" ", "")
                            data = data.substring(1)
                            try {
                                val humidity = data.toFloat()
                                listener?.onHumidityChanged(humidity)
                            } catch (nfe: NumberFormatException) {

                            }
                        }
                    }
                } catch (e: IOException) {
                    Log.w(TAG, "Unable to access UART device", e)
                }

                // Continue listening for more interrupts
                return true
            }

            override fun onUartDeviceError(uart: UartDevice?, error: Int) {
                Log.w(TAG, "$uart: Error event $error")
            }
        })
    }

    private fun readUartBuffer(uartDevice: UartDevice): String {
        // Maximum amount of data to read at one time
        val maxCount = 8
        val str = StringBuilder()
        uartDevice.apply {
            ByteArray(maxCount).also { buffer ->
                var count: Int = read(buffer, buffer.size)
                str.append(buffer.toString(Charset.defaultCharset()))
                while (count > 0) {
                    count = read(buffer, buffer.size)
                    str.append(buffer.toString(Charset.defaultCharset()))
                }
            }
        }
        return str.toString()

    }

    fun setOnValueChangedListener(listener: ValueChangedListener?) {
        this.listener = listener
    }

    interface ValueChangedListener {
        fun onTemperatureChangedChanged(newValue: Float)
        fun onHumidityChanged(newValue: Float)
    }
}