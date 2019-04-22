package com.taishoberius.rised.meteo.viewmodel

import android.util.Log
import com.taishoberius.rised.cross.viewmodel.BaseCardViewModel
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import com.taishoberius.rised.cross.com.ServiceManager
import io.reactivex.disposables.Disposable

class MeteoCardViewModel : BaseCardViewModel(), IMeteoCardViewModel {

    private var forecastDisposable: Disposable

    init {
        forecastDisposable = RxBus.listen(RxEvent.ForecastEvent::class.java).subscribe() { event ->
            manageEvent(event)
        }
    }



    override fun onCardViewDetached() {
        super.onCardViewDetached()
        if (!forecastDisposable.isDisposed) forecastDisposable.dispose()
    }

    override fun getCurrendWeather() {
        //TODO get the current city
        ServiceManager.getForecastService().getCurrentWeather("Paris,fr")
    }

    private fun manageEvent(event: RxEvent.ForecastEvent?) {
        event?.let {e ->
            if (!e.success) return
            //TODO faire qqch avec l'event
        }
    }
}