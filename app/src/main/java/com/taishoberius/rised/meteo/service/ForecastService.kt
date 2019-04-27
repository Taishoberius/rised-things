package com.taishoberius.rised.meteo.service

import android.util.Log
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import com.taishoberius.rised.cross.com.URLManager
import com.taishoberius.rised.meteo.com.ForecastServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ForecastService: IForecastService {

    private val TAG = "ForecastService"

    override fun getCurrentWeather(city: String) {
        val service = ForecastServer.makeCurrentForecastService()
        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getCurrentWeatherFromCityNameAsync(appid = URLManager.appId, cityName = city)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful) {
                        RxBus.publish(RxEvent.ForecastEvent(true, response.body()))
                    } else {
                        RxBus.publish(RxEvent.ForecastEvent(false, null))
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "HTTPException : ${e.message()}")
                } catch (e: Throwable) {
                    Log.e(TAG, "getCurrentWeather(city: String) failed :")
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getCurrentWeather(cityId: Int) {
        val service = ForecastServer.makeCurrentForecastService()
        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getCurrentWeatherFromCityIdAsync(appid = URLManager.appId, cityId = cityId)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful) {
                        RxBus.publish(RxEvent.ForecastEvent(true, response.body()))
                    } else {
                        RxBus.publish(RxEvent.ForecastEvent(false, null))
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "HTTPException : ${e.message()}")
                } catch (e: Throwable) {
                    Log.e(TAG, "getCurrentWeather(cityId: Int) failed :")
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getCurrentWeather(lat: Double, lon: Double) {
        val service = ForecastServer.makeCurrentForecastService()
        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getCurrentWeatherFromCityLatLonAsync(appid = URLManager.appId, cityLat = lat, cityLon = lon)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful) {
                        RxBus.publish(RxEvent.ForecastEvent(true, response.body()))
                    } else {
                        RxBus.publish(RxEvent.ForecastEvent(false, null))
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "HTTPException : ${e.message()}")
                } catch (e: Throwable) {
                    Log.e(TAG, "getCurrentWeather(lat: Double, lon: Double) failed :")
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getFiveDaysWeather(city: String) {
        val service = ForecastServer.makeCurrentForecastService()
        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getFiveDaysForecastForCityNameAsync(appid = URLManager.appId, cityName = city)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful) {
                        RxBus.publish(RxEvent.ForecastListEvent(true, response.body()))
                    } else {
                        RxBus.publish(RxEvent.ForecastListEvent(false, null))
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "HTTPException : ${e.message()}")
                } catch (e: Throwable) {
                    Log.e(TAG, "getFiveDaysWeather(city: String) failed :")
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getFiveDaysWeather(cityId: Int) {
        val service = ForecastServer.makeCurrentForecastService()
        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getFiveDaysForecastForCityIdAsync(appid = URLManager.appId, cityId = cityId)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful) {
                        RxBus.publish(RxEvent.ForecastListEvent(true, response.body()))
                    } else {
                        RxBus.publish(RxEvent.ForecastListEvent(false, null))
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "HTTPException : ${e.message()}")
                } catch (e: Throwable) {
                    Log.e(TAG, "getFiveDaysWeather(cityId: Int) failed :")
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getFiveDaysWeather(lat: Double, lon: Double) {
        val service = ForecastServer.makeCurrentForecastService()
        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getFiveDaysForecastForCityLatLonAsync(appid = URLManager.appId, cityLat = lat, cityLon = lon)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful) {
                        RxBus.publish(RxEvent.ForecastListEvent(true, response.body()))
                    } else {
                        RxBus.publish(RxEvent.ForecastListEvent(false, null))
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "HTTPException : ${e.message()}")
                } catch (e: Throwable) {
                    Log.e(TAG, "getFiveDaysWeather(lat: Double, lon: Double) failed :")
                    e.printStackTrace()
                }
            }
        }
    }
}