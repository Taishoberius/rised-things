package com.taishoberius.rised

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.media.MediaMetadata
import android.os.Bundle
import android.preference.Preference
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
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import com.taishoberius.rised.main.main.model.Preferences
import com.taishoberius.rised.main.main.services.PreferenceService
import com.taishoberius.rised.sensors.Arduino
import com.taishoberius.rised.sensors.MotionSensor
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity: BluetoothActivity(), BluetoothMediaDelegate, BluetoothProfileDelegate {
    private lateinit var preferenceDisposable: Disposable
    private var token: String = ""
    private val TAG = "MainActivity"
    private lateinit var motionSensor: MotionSensor
    private lateinit var arduino: Arduino
    private var connectedDevice: BluetoothDevice? = null
    private var userPreferences: List<Preferences>? = null
    private var preference: Preferences? = null

    private fun setFirebaseToken(preferences: Preferences) {
        Log.w(TAG, "setFirebaseToken")
        if (preferences.token != null && preferences.token == this.token) return
        val id = preferences.id ?: return

        preferences.token = this.token
        retrofit.create(PreferenceService::class.java)
            .updPreference(id, preferences)
            .enqueue(object: Callback<Preferences> {
                override fun onFailure(call: Call<Preferences>, t: Throwable) {

                }

                override fun onResponse(call: Call<Preferences>, response: Response<Preferences>) {
                    Log.w(TAG, "successfully set token")
                }
            })
    }

    private lateinit var retrofit: Retrofit

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

        this.retrofit = Retrofit.Builder()
            .baseUrl("https://us-central1-erised-e4dae.cloudfunctions.net")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        initView()
        initSensors()
        initListener()
        sleepMode(false)
        this.getUserPreferences()
        this.bluetoothMediaDelegate = this
        this.bluetoothProfileDelegate = this

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                this.token = task.result?.token ?: ""

                // Log and toast
                Log.d("LETOKEN", token)
            })

        preferenceDisposable = RxBus.listen(RxEvent.PreferenceNotificationEvent::class.java).subscribe {
            Log.w(TAG, "preference notification receivced " + this.preference?.id + this.preference + " " + it.preferenceUpdatedId)
            if (preference?.id == it.preferenceUpdatedId) {
                getPreferenceWithId(it.preferenceUpdatedId);
            }
        }
    }

    private fun getUserPreferences() {
        retrofit
            .create(PreferenceService::class.java)
            .getPreferences()
            .enqueue(object: Callback<List<Preferences>> {
                override fun onFailure(call: Call<List<Preferences>>, t: Throwable) {}

                override fun onResponse(
                    call: Call<List<Preferences>>,
                    response: Response<List<Preferences>>
                ) {
                    response.body()?.also {
                        Log.w(this@MainActivity.TAG, it.toString())
                        this@MainActivity.userPreferences = it
                    }
                }
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
            findAndConnectNearestDevice()
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

        getPreferenceForDevice(device?.name)
        RxBus.publish(RxEvent.BluetoothProfileEvent(device, BluetoothState.CONNECTED))
    }

    private fun getPreferenceForDevice(name: String?) {
        Log.w(TAG, name)
        val target = name ?: ""
        if (target.isEmpty()) return


        val id = getPreferenceIdIfExist(target)
        Log.w(TAG, "id: $id")

        if (id.isEmpty()) {
            userPreferences?.also {
                val first = it.first { preferences -> preferences.deviceName == name }
                Log.w(TAG, first.toString())
                this.preference = first
                savePreferenceId(first.deviceName, first.id)
            }
        }

        getPreferenceWithId(id)
    }

    private fun savePreferenceId(name: String?, id: String?) {
        if (id == null) return
        if (name == null) return

        val prefs = getSharedPreferences("ids", Context.MODE_PRIVATE)
        prefs.edit().putString(name, id).apply()
    }

    private fun getPreferenceWithId(id: String) {
        retrofit.create(PreferenceService::class.java)
            .getPreference(id)
            .enqueue(object : Callback<Preferences> {
                override fun onFailure(call: Call<Preferences>, t: Throwable) {
                    Log.w(TAG, "failed retrieve preference")
                }

                override fun onResponse(call: Call<Preferences>, response: Response<Preferences>) {
                    response.body().also {
                        Log.w(TAG, "fetched preference for id $it")
                        if (it != null) {
                            this@MainActivity.preference = it
                            setFirebaseToken(it)
                            RxBus.publish(RxEvent.PreferenceEvent(it))
                        }
                    }
                }
            })
    }

    private fun getPreferenceIdIfExist(name: String): String {
        val prefs = getSharedPreferences("ids", Context.MODE_PRIVATE)
        if (prefs.contains(name)) {
            val s = prefs.getString(name, "")
            return s
        }

        return ""
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
