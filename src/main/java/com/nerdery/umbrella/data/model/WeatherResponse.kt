package com.nerdery.umbrella.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response from DarkSky weather requests in [com.nerdery.umbrella.data.api.WeatherService]
 */
data class WeatherResponse (

    /**
     * Current Weather Condition
     * @return ForecastCondition
     */
    @SerializedName("currently")
    var currentForecast: ForecastCondition?,
    /**
     * Hourly Response model that contains list of ForecastConditions
     * @return HourlyResponse
     */
    var hourly: HourlyResponse?
)
