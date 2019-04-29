package com.taishoberius.rised.meteo.viewmodel

import androidx.lifecycle.LiveData
import com.taishoberius.rised.cross.viewmodel.IBaseCardViewModel
import com.taishoberius.rised.meteo.model.Forecast

interface IMeteoCardViewModel: IBaseCardViewModel {
    fun launchGetWeather()

    fun getCurrentWeatherLiveData(): LiveData<Forecast>

    fun getFiveDaysForecastLiveData(): LiveData<List<Forecast>>
}