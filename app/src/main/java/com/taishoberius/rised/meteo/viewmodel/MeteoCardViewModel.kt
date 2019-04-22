package com.taishoberius.rised.meteo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.taishoberius.rised.cross.viewmodel.BaseCardViewModel
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import com.taishoberius.rised.cross.com.ForecastRetrofitBuilder
import com.taishoberius.rised.cross.com.ServiceManager
import com.taishoberius.rised.meteo.model.Forecast
import io.reactivex.disposables.Disposable

class MeteoCardViewModel : BaseCardViewModel(), IMeteoCardViewModel {

    //================================================================================
    // Attributes
    //================================================================================
    private var currentWeatherDisposable: Disposable
    private var fiveDaysForecastDisposable: Disposable

    private val currentWeatherLiveData = MutableLiveData<Forecast>()
    private val fiveDaysForecastLiveData = MutableLiveData<List<Forecast>>()

    //================================================================================
    // Constructor
    //================================================================================

    init {
        currentWeatherDisposable = RxBus.listen(RxEvent.ForecastEvent::class.java).subscribe { event ->
            manageCurrentWeatherEvent(event)
        }
        fiveDaysForecastDisposable = RxBus.listen(RxEvent.ForecastListEvent::class.java).subscribe { event ->
            manageFiveDaysForecastEvent(event)
        }
    }

    //================================================================================
    // Lifecycle
    //================================================================================

    override fun onCardViewDetached() {
        super.onCardViewDetached()
        if (!currentWeatherDisposable.isDisposed) currentWeatherDisposable.dispose()
        if (!fiveDaysForecastDisposable.isDisposed) fiveDaysForecastDisposable.dispose()
    }

    //================================================================================
    // Overrides from IMeteoCardViewModel
    //================================================================================

    override fun launchGetForecast() {
        ServiceManager.getForecastService().getFiveDaysWeather("Paris")
    }

    override fun launchGetCurrentWeather() {
        //TODO get the current city
        ServiceManager.getForecastService().getCurrentWeather("Paris,fr")
    }

    override fun getCurrentWeatherLiveData(): LiveData<Forecast> {
        return currentWeatherLiveData
    }

    override fun getfiveDaysForecastLiveData(): LiveData<List<Forecast>> {
        return fiveDaysForecastLiveData
    }

    //================================================================================
    // Managing Rx events
    //================================================================================

    private fun manageCurrentWeatherEvent(event: RxEvent.ForecastEvent?) {
        event?.let {e ->
            if (!e.success) return
            //TODO faire qqch avec l'event
        }
    }
    private fun manageFiveDaysForecastEvent(event: RxEvent.ForecastListEvent?) {
        event?.let {e ->
            if (!e.success) return
            //TODO faire qqch avec l'event
        }
    }
}