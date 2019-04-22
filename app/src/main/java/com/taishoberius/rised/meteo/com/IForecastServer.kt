package com.taishoberius.rised.meteo.com

import com.taishoberius.rised.meteo.model.Forecast
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IForecastServer {

    @GET("/data/2.5/weather")
    fun getCurrentWeatherFromCityNameAsync(
        @Query("appid") appid: String,
        @Query("q") cityName: String
    ): Deferred<Response<Forecast>>

    @GET("/data/2.5/weather")
    fun getCurrentWeatherFromCityIdAsync(
        @Query("appid") appid: String,
        @Query("id") cityId: Int
    ): Deferred<Response<Forecast>>

    @GET("/data/2.5/weather")
    fun getCurrentWeatherFromCityLatLonAsync(
        @Query("appid") appid: String,
        @Query("lat") cityLat: Double,
        @Query("lon") cityLon: Double
    ): Deferred<Response<Forecast>>

    @GET("/data/2.5/weather")
    fun getFiveDaysForecastForCityNameAsync(
        @Query("appid") appid: String,
        @Query("q") cityName: String
    ): Deferred<Response<List<Forecast>>>

    @GET("/data/2.5/weather")
    fun getFiveDaysForecastForCityIdAsync(
        @Query("appid") appid: String,
        @Query("id") cityId: Int
    ): Deferred<Response<List<Forecast>>>

    @GET("/data/2.5/weather")
    fun getFiveDaysForecastForCityLatLonAsync(
        @Query("appid") appid: String,
        @Query("lat") cityLat: Double,
        @Query("lon") cityLon: Double
    ): Deferred<Response<List<Forecast>>>

}