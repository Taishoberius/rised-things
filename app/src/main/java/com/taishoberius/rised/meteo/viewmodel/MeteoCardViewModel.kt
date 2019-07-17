package com.taishoberius.rised.meteo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import com.taishoberius.rised.cross.com.ServiceManager
import com.taishoberius.rised.cross.utils.MeteoUtils
import com.taishoberius.rised.cross.viewmodel.BaseCardViewModel
import com.taishoberius.rised.main.main.model.Preferences
import com.taishoberius.rised.main.main.utils.AddressUtil
import com.taishoberius.rised.meteo.model.Forecast
import io.reactivex.disposables.Disposable

class MeteoCardViewModel : BaseCardViewModel(), IMeteoCardViewModel {

    //================================================================================
    // Attributes
    //================================================================================
    private val TAG = "MeteoCardViewModel"
    private var currentWeatherDisposable: Disposable
    private var fiveDaysForecastDisposable: Disposable
    private var currentPreferenceDisposable: Disposable

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
        currentPreferenceDisposable = RxBus.listen(RxEvent.PreferenceEvent::class.java).subscribe {
            manageCurrentPreference(it)
        }
    }

    private var preference: Preferences? = null

    private fun manageCurrentPreference(preferenceEvent: RxEvent.PreferenceEvent?) {
        this.preference = preferenceEvent?.preference
        launchGetWeather()
    }

    //================================================================================
    // Lifecycle
    //================================================================================

    override fun onCardViewDetached() {
        super.onCardViewDetached()
        if (!currentWeatherDisposable.isDisposed) currentWeatherDisposable.dispose()
        if (!fiveDaysForecastDisposable.isDisposed) fiveDaysForecastDisposable.dispose()
        if (!currentPreferenceDisposable.isDisposed) currentPreferenceDisposable.dispose()
    }

    //================================================================================
    // Overrides from IMeteoCardViewModel
    //================================================================================

    override fun launchGetWeather() {
        //TODO get the current city
        val city = (AddressUtil.getCity(preference?.address) ?: "Paris") + ",fr"
        ServiceManager.getForecastService().getFiveDaysWeather(city)
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
           val fc = MeteoUtils.filterOneByDay(e.forecasts!!, preference?.weather ?: 0)
            fiveDaysForecastLiveData.value = when {
                e.success -> fc
                else -> null
            }
        }
    }
}