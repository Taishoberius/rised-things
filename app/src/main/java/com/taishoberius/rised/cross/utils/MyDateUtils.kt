package com.taishoberius.rised.cross.utils

import java.util.*

object MyDateUtils {

    fun getToday(plus: Int = 0): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, plus)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}"
    }
}