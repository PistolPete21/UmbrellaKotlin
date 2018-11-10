package com.nerdery.umbrella.util

import java.text.SimpleDateFormat
import java.util.*

object DateTime {
    val timeFormatter = SimpleDateFormat("h:00 a", Locale.ENGLISH)
    val dayFormatter = SimpleDateFormat("d", Locale.ENGLISH)
    val dateFormatter = SimpleDateFormat("EEE MMM d", Locale.ENGLISH )

    fun convertDateToString(dateTime: Date?, formatter: SimpleDateFormat): String {
        try {
            return formatter.format(dateTime)
        } catch (ex: Exception) {
            return ""
        }

    }
}
