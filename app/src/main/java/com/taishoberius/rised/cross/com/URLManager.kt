package com.taishoberius.rised.cross.com

object URLManager {

    public val appId = "57aa455fa23584a2539c7e50784767e9"

    private val baseForecast = "https://api.openweathermap.org/data/2.5/"
    private val forecastCurrent = "weather"
    private val forecast5days = "forecast"

    fun getForecastCurrentURL(): String {
        return baseForecast + forecastCurrent
    }

    fun getBaseForecastURL(): String {
        return baseForecast
    }
}