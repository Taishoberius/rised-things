package com.taishoberius.rised.cross.viewmodel

import androidx.lifecycle.ViewModel

open class BaseCardViewModel: ViewModel() {

    open fun onCardViewDetached() {
        onCleared()
    }


}