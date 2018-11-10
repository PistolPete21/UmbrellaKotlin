package com.nerdery.umbrella.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Specific weather condition for time and location
 */
data class ForecastCondition(
        /**
         * Text summary of weather condition
         * @return Summary
         */
        var summary: String?,
        /**
         * Icon name of weather condition
         * @return Icon Name
         */
        var icon: String?,
        /**
         * Temperature in degrees of [TempUnit] sent during request
         * @return Temperature
         */
        @SerializedName("temperature") var temp: Double = 0.0,
        /**
         * Time/Date of Forecast Condition
         * @return Date
         */
        var time: Date?
)