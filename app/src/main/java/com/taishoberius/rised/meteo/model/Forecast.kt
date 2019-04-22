package com.taishoberius.rised.meteo.model

data class Forecast(
    private val dt: Double,
    private val main: Main,
    private val weather: List<Weather>,
    private val clouds: Cloud,
    private val wind: Wind,
    private val rain: Rain,
    private val snow: Snow
)