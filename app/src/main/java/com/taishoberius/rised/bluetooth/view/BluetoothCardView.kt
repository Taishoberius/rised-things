package com.taishoberius.rised.bluetooth.view

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.taishoberius.rised.bluetooth.models.BluetoothInfo
import com.taishoberius.rised.bluetooth.models.BluetoothState
import com.taishoberius.rised.bluetooth.viewmodel.BluetoothViewModel
import com.taishoberius.rised.bluetooth.viewmodel.IBluetoothViewModel
import com.taishoberius.rised.cross.view.BaseCardView
import kotlinx.android.synthetic.main.bluetooth.view.*

class BluetoothCardView: BaseCardView, LifecycleOwner {

    //================================================================================
    // Attributes
    //================================================================================

    private var model: IBluetoothViewModel = BluetoothViewModel()
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private val TAG = "BluetoothCardView"
    private val notConnectedInfo = context.getString(com.taishoberius.rised.R.string.no_device_connected)
    private var bluetoothInfoText = notConnectedInfo

    //================================================================================
    // Constructors
    //================================================================================

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
    }

    //================================================================================
    // LifeCycle
    //================================================================================

    override fun onAttachedToWindow() {
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
        super.onAttachedToWindow()
        model.getMediaInfoLiveData().observe(this, Observer {
            manageBluetoothInfo(it)
        })
    }

    override fun onDetachedFromWindow() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
        model.getMediaInfoLiveData().removeObservers(this)
        super.onDetachedFromWindow()
    }

    //================================================================================
    // Business method
    //================================================================================

    private fun manageBluetoothInfo(info: BluetoothInfo) {
        manageBluetoothText(info.info)
        manageBluetoothMediaState(info.state)
    }

    private fun manageBluetoothText(info: String?) {
        info?.run { bluetooth_info.text = this }
    }

    private fun manageBluetoothMediaState(state: BluetoothState?) {
        val paused = com.taishoberius.rised.R.drawable.ic_pause
        val play = com.taishoberius.rised.R.drawable.ic_play
        val bluetoothIcon = com.taishoberius.rised.R.drawable.ic_bluetooth
        state?.run {
            when (this) {
                BluetoothState.MEDIA_PLAYING -> bluetooth_state.setImageResource(play)
                BluetoothState.MEDIA_PAUSED -> bluetooth_state.setImageResource(paused)
                else -> bluetooth_state.setImageResource(bluetoothIcon)
            }

            if (this == BluetoothState.DISCONNECTED)
                bluetooth_info.text = notConnectedInfo
        }
    }

    override fun initView() {

    }

    override fun initListener() {

    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}