package com.taishoberius.rised.trajet.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.taishoberius.rised.trajet.model.Json4Kotlin_Base

public interface DirectionService {

    @GET("directions/json?")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String
    ): Call<Json4Kotlin_Base>
}