package com.taishoberius.rised.cross.Rx

import com.taishoberius.rised.meteo.model.Forecast

class RxEvent {
    data class ForecastEvent(
        val success: Boolean,
        val forecast: Forecast?
    )

    class ForecastListEvent(
        val success: Boolean,
        val forecasts: List<Forecast>?
    )
}