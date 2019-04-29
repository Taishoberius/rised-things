package com.taishoberius.rised.cross.utils

import android.text.TextUtils
import androidx.annotation.DrawableRes
import com.taishoberius.rised.R
import com.taishoberius.rised.meteo.model.Forecast
import kotlin.math.roundToInt

object MeteoUtils {

    @DrawableRes
    fun getIcon(id: Int): Int{
        return when(id) {
            // Thunder
            200, 201, 202, 210, 211, 212, 221, 230, 231, 232 -> R.drawable.ic_thunder
            // Rain and Drizzle
            300, 301, 500, 501 -> R.drawable.ic_rainy_1
            302, 502 -> R.drawable.ic_rainy_2
            310, 510 -> R.drawable.ic_rainy_3
            311, 511 -> R.drawable.ic_rainy_4
            312, 512 -> R.drawable.ic_rainy_5
            313, 513 -> R.drawable.ic_rainy_6
            314, 321, 514, 521 -> R.drawable.ic_rainy_7
            // Snow
            600 -> R.drawable.ic_snowy_1
            601, 602 -> R.drawable.ic_snowy_2
            611, 612 -> R.drawable.ic_snowy_3
            613, 615 -> R.drawable.ic_snowy_4
            616, 620 -> R.drawable.ic_snowy_5
            621, 622 -> R.drawable.ic_snowy_6
            // Atmosphere
            701, 711, 721, 731, 741, 751, 761, 762, 771, 781 -> R.drawable.ic_cloudy
            // Clear sky
            800 -> R.drawable.ic_day
            // Clouds
            801 -> R.drawable.ic_cloudy_day_1
            802 -> R.drawable.ic_cloudy_day_2
            803 -> R.drawable.ic_cloudy_day_3
            804 -> R.drawable.ic_cloudy
            else -> R.drawable.ic_day
        }
    }

    fun filterOneByDay(fullList: List<Forecast>) : List<Forecast> {
        val newList = mutableListOf<Forecast>()

        fullList.forEach {fcast ->
            if (!newList.contains(fcast.dt_txt)) {
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