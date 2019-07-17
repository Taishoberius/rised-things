package com.taishoberius.rised.cross.utils

import android.text.TextUtils
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.taishoberius.rised.R
import com.taishoberius.rised.meteo.model.Forecast
import kotlinx.coroutines.newSingleThreadContext
import kotlin.math.roundToInt

object MeteoUtils {

    @RawRes
    fun getIcon(id: Int): Int{
        return when(id) {
            // Thunder
            200, 201, 202, 210, 211, 212, 221, 230, 231, 232 -> R.raw.thunder
            // Rain and Drizzle
            300, 301, 500, 501 -> R.raw.rainy_day
            302, 502 -> R.raw.rainy_day
            310, 510 -> R.raw.rainy_day
            311, 511 -> R.raw.rainy_day
            312, 512 -> R.raw.rainy_day
            313, 513 -> R.raw.rainy_day
            314, 321, 514, 521 -> R.raw.thunder_rain
            // Snow
            600 -> R.raw.snowy
            601, 602 -> R.raw.snowy
            611, 612 -> R.raw.snowy
            613, 615 -> R.raw.snowy
            616, 620 -> R.raw.snowy
            621, 622 -> R.raw.snowy
            // Atmosphere
            701, 711, 721, 731, 741, 751, 761, 762, 771, 781 -> R.raw.cloudy
            // Clear sky
            800 -> R.raw.day
            // Clouds
            801 -> R.raw.day
            802 -> R.raw.day
            803 -> R.raw.day
            804 -> R.raw.day
            else -> R.raw.day
        }
    }

    fun filterOneByDay(fullList: List<Forecast>, nbDay: Int) : List<Forecast> {
        val newList = mutableListOf<Forecast>()

        fullList.forEach {fcast ->
            if (!newList.contains(fcast.dt_txt) && newList.size < nbDay) {
                newList.add(fcast)
            }
        }

        return newList
    }

    fun MutableList<Forecast>.contains(str: String): Boolean {
        this.forEach { currFcast ->
            if (TextUtils.equals(currFcast.dt_txt.substring(0, 10), str.substring(0, 10))) {
                return true
            }
        }
        return false
    }

    fun kelvinToCelsius(kelvin: Double): String {
        return "${(kelvin - 273.15).roundToInt()}°C"
    }
}