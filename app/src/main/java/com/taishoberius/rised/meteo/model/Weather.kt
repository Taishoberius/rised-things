package com.taishoberius.rised.meteo.model

data class Weather(
    private val id: Double,
    private val main: String,
    private val description: String,
    private val icon: String
)