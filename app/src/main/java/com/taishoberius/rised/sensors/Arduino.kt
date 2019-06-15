package com.taishoberius.rised.sensors

import android.util.Log
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.UartDevice
import com.google.android.things.pio.UartDeviceCallback
import java.io.IOException
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.nio.charset.Charset

class Arduino(uartPin: String = "UART1") {
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
            setBaudrate(115200)
            setDataSize(8)
            setParity(UartDevice.PARITY_NONE)
            setStopBits(1)
        }
        mDevice?.registerUartDeviceCallback(object : UartDeviceCallback {
            override fun onUartDeviceDataAvailable(uart: UartDevice): Boolean {
                // Read available data from the UART device
                try {
                    var data = readUartBuffer(uart).trim()
                    Log.v(TAG, "Received $data from Arduino")

                    var array = data.split('/')
                    try {
                        listener?.onTemperatureChangedChanged(data.split('/')[0].toFloat())
                        listener?.onHumidityChanged(data.split('/')[1].toFloat())
                    } catch (nfe: NumberFormatException) {
                        Log.v(TAG, "Failed to send the temperature or humidity (NumberFormatException)")
                    } catch (ioobe: IndexOutOfBoundsException) {
                        Log.v(TAG, "Failed to send the temperature or humidity (IndexOutOfBoundsException)")
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
        val strBuilder = StringBuilder()
        uartDevice.apply {
            ByteArray(maxCount).also { buffer ->
                var count: Int = read(buffer, buffer.size)
                strBuilder.append(buffer.toString(Charset.defaultCharset()))
                while (count > 0) {
                    count = read(buffer, buffer.size)
                    strBuilder.append(buffer.toString(Charset.defaultCharset()))
                }
            }
        }
        var str = strBuilder.toString()
        if (strBuilder.toString().indexOf('\r') != -1) {
            str = str.substring(0, strBuilder.toString().indexOf('\r'))
        }
        return str

    }

    fun setOnValueChangedListener(listener: ValueChangedListener?) {
        this.listener = listener
    }

    interface ValueChangedListener {
        fun onTemperatureChangedChanged(newValue: Float)
        fun onHumidityChanged(newValue: Float)
    }
}