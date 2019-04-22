package com.taishoberius.rised.meteo.viewmodel

import androidx.lifecycle.LiveData
import com.taishoberius.rised.cross.viewmodel.IBaseCardViewModel
import com.taishoberius.rised.meteo.model.Forecast

interface IMeteoCardViewModel: IBaseCardViewModel {
    fun launchGetCurrentWeather()

    fun launchGetForecast()

    fun getCurrentWeatherLiveData(): LiveData<Forecast>

    fun getfiveDaysForecastLiveData(): LiveData<List<Forecast>>
}