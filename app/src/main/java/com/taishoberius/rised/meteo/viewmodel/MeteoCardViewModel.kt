package com.taishoberius.rised.meteo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.taishoberius.rised.cross.viewmodel.BaseCardViewModel
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import com.taishoberius.rised.cross.com.ForecastRetrofitBuilder
import com.taishoberius.rised.cross.com.ServiceManager
import com.taishoberius.rised.cross.utils.MeteoUtils
import com.taishoberius.rised.meteo.model.Forecast
import io.reactivex.disposables.Disposable

class MeteoCardViewModel : BaseCardViewModel(), IMeteoCardViewModel {

    //================================================================================
    // Attributes
    //================================================================================
    private val TAG = "MeteoCardViewModel"
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

    override fun launchGetWeather() {
        //TODO get the current city
        //ServiceManager.getForecastService().getCurrentWeather("Paris,fr")
        ServiceManager.getForecastService().getFiveDaysWeather("Paris,fr")
    }

    override fun getCurrentWeatherLiveData(): LiveData<Forecast> {
        return currentWeatherLiveData
    }

    override fun getFiveDaysForecastLiveData(): LiveData<List<Forecast>> {
        return fiveDaysForecastLiveData
    }

    //================================================================================
    // Managing Rx events
    //================================================================================

    private fun manageCurrentWeatherEvent(event: RxEvent.ForecastEvent?) {
        event?.let {e ->
            currentWeatherLiveData.value = when {
                e.success ->  e.forecast
                else -> null
            }
        }
    }
    private fun manageFiveDaysForecastEvent(event: RxEvent.ForecastListEvent?) {
        event?.let {e ->
            //TODO ("remove useless data from the forecast list")
           val fc = MeteoUtils.filterOneByDay(e.forecasts!!)
            fiveDaysForecastLiveData.value = when {
                e.success -> fc
                else -> null
            }
        }
    }
}