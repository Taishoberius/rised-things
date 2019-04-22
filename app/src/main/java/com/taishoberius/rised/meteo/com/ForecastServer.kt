package com.taishoberius.rised.meteo.com

import com.taishoberius.rised.cross.com.ForecastRetrofitBuilder

object ForecastServer {

    fun makeCurrentForecastService(): IForecastServer {
        return ForecastRetrofitBuilder.getForecastRetrofit().create(IForecastServer::class.java)
    }
}