package com.taishoberius.rised.trajet

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import Json4Kotlin_Base
import Legs

public interface DirectionService {

    @GET("directions/json?")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String
    ): Call<Json4Kotlin_Base>
}