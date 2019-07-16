package com.taishoberius.rised.main.main.model

data class Preferences (
    val deviceName: String?,
    val name: String?,
    val weather: Int?,
    val itinerary: Boolean?,
    val humidity: Boolean?,
    val temperature: Boolean?,
    val address: String?,
    val transportType: String?,
    val workAddress: String?,
    val id: String?
)