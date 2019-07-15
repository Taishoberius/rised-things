package com.taishoberius.rised.main.services

import com.taishoberius.rised.main.services.model.Preferences
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PreferenceService {
    @GET("getPreferences")
    fun getPreferences(): Call<List<Preferences>>
    @GET("getPreference")
    fun getPreference(@Query("id") id: String): Call<Preferences>
}