package com.taishoberius.rised.meteo.com

import com.taishoberius.rised.cross.com.ForecastRetrofitBuilder

object ForecastServer {

    fun makeCurrentWeatherForecastService(): IForecastServer {
        return ForecastRetrofitBuilder.getCurrentWeatherRetrofit().create(IForecastServer::class.java)
    }

    fun makeFiceDaysForecastService(): IForecastServer {
        return ForecastRetrofitBuilder.getFiveDaysForecastRetrofit().create(IForecastServer::class.java)
    }
}