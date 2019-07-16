package com.taishoberius.rised.main.main.services

import com.taishoberius.rised.main.main.model.Preferences
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PreferenceService {
    @GET("getPreferences")
    fun getPreferences(): Call<List<Preferences>>
    @GET("getPreference")
    fun getPreference(@Query("id") id: String): Call<Preferences>
    @POST("updPreference")
    fun updPreference(@Query("id") id: String, @Body preferences: Preferences): Call<Preferences>
}