package com.taishoberius.rised.meteo.model

data class Forecast(
    val dt: Double,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Cloud,
    val wind: Wind,
    val rain: Rain,
    val snow: Snow,
    val dt_txt: String
)