package com.taishoberius.rised.bluetooth.viewmodel

import android.media.MediaMetadata
import androidx.lifecycle.MutableLiveData
import com.taishoberius.rised.bluetooth.models.BluetoothInfo
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import com.taishoberius.rised.cross.viewmodel.IBaseCardViewModel
import io.reactivex.disposables.Disposable

public class BluetoothViewModel: IBluetoothViewModel, IBaseCardViewModel {
    //================================================================================
    // Attributes
    //================================================================================
    private val TAG = "BluetoothCardViewModel"
    private var currentMediaInfoDisposable: Disposable
    private var currentProfileInfoDisposable: Disposable

    private val bluetoothInfoLiveData = MutableLiveData<BluetoothInfo>()
    private var bluetoothInfo: BluetoothInfo = BluetoothInfo("", null)

    //================================================================================
    // Constructor
    //================================================================================

    init {
        currentMediaInfoDisposable= RxBus.listen(RxEvent.BluetoothMediaEvent::class.java).subscribe { event ->
            manageBluetoothMediaEvent(event)
        }
        currentProfileInfoDisposable = RxBus.listen(RxEvent.BluetoothProfileEvent::class.java).subscribe {
            manageBluetoothProfileEvent(it)
        }
    }

    //================================================================================
    // Lifecycle
    //================================================================================

    override fun onCardViewDetached() {
        if (!currentMediaInfoDisposable.isDisposed) currentMediaInfoDisposable.dispose()
    }

    //================================================================================
    // Overrides from IBluetoothCardViewModel
    //================================================================================

    override fun getMediaInfoLiveData(): MutableLiveData<BluetoothInfo> {
        return bluetoothInfoLiveData
    }

    //================================================================================
    // Managing Rx events
    //================================================================================

    private fun manageBluetoothMediaEvent(event: RxEvent.BluetoothMediaEvent?) {
        event?.let { e ->
            var info = ""
            val state = e.state
            e.media?.run { info += getString(MediaMetadata.METADATA_KEY_TITLE) + " " + getString(MediaMetadata.METADATA_KEY_ARTIST)}
            if (!info.isEmpty() && info != bluetoothInfo.info)
                bluetoothInfo.info = info

            if (state != null && state != bluetoothInfo.state)
                bluetoothInfo.state = state

            bluetoothInfoLiveData.value = bluetoothInfo
        }
    }

    private fun manageBluetoothProfileEvent(event: RxEvent.BluetoothProfileEvent?) {
        event?.let {
            it.device?.run { bluetoothInfo.info = this.name }
            it.state?.run { bluetoothInfo.state = this }

            bluetoothInfoLiveData.value = bluetoothInfo
        }
    }
}