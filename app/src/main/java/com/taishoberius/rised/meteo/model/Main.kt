package com.taishoberius.rised.meteo.model

data class Main(
    private val temp: Double,
    private val temp_min: Double,
    private val temp_max: Double,
    private val pressure: Double,
    private val sea_level: Double,
    private val grnd_level: Double,
    private val humidity: Double,
    private val temp_kf: Double
)