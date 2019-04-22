package com.taishoberius.rised.meteo.viewmodel

import com.taishoberius.rised.cross.viewmodel.IBaseCardViewModel

interface IMeteoCardViewModel: IBaseCardViewModel {
    fun getCurrendWeather()
}