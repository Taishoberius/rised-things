package com.taishoberius.rised.notes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import com.taishoberius.rised.cross.viewmodel.IBaseCardViewModel

class NotesViewModel: IBaseCardViewModel {

    private val mutableNotesLiveData : MutableLiveData<String> = MutableLiveData()
    val notesLiveData: LiveData<String> = mutableNotesLiveData
    private val disposable = RxBus.listen(RxEvent.PreferenceEvent::class.java).subscribe {
        if (it.preference.notes == true) {
            mutableNotesLiveData.value = it.preference.notesText ?: ""
        }
    }

    override fun onCardViewDetached() {
        disposable.dispose()
    }
}