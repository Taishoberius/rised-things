package com.taishoberius.rised.cross.com

import com.taishoberius.rised.meteo.service.ForecastService

object ServiceManager {

    private var forecastService: ForecastService? = null

    fun getForecastService(): ForecastService {
        if (forecastService == null)
            forecastService = ForecastService()
        return forecastService!!
    }
}