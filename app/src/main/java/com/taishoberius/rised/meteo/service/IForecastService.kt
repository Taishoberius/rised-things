package com.taishoberius.rised.meteo.service

interface IForecastService {

    /**
     * Posts event with current weather
     * @param city the name of the searched city
     */
    fun getCurrentWeather(city: String)

    /**
     * Posts event with current weather
     * @param cityId the id of the city, defined in a JSON file
     */
    fun getCurrentWeather(cityId: Int)

    /**
     * Posts event with current weather
     * @param lat the latitude of the searched city
     * @param lon the longitude of the searched city
     */
    fun getCurrentWeather(lat: Double, lon: Double)

    /**
     * Posts event with five days forecast
     * @param city the name of the searched city
     */
    fun getFiveDaysWeather(city: String)

    /**
     * Posts event with five days forecast
     * @param cityId the id of the city, defined in a JSON file
     */
    fun getFiveDaysWeather(cityId: Int)

    /**
     * Posts event with five days forecast
     * @param lat the latitude of the searched city
     * @param lon the longitude of the searched city
     */
    fun getFiveDaysWeather(lat: Double, lon: Double)
}