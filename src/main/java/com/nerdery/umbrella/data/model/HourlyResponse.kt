package com.nerdery.umbrella.data.model

import com.google.gson.annotations.SerializedName

data class HourlyResponse(
    /**
     * Ordered List of Hourly [ForecastCondition]
     * @return List of [ForecastCondition]
     */
    @SerializedName("data")
    var hours: List<ForecastCondition>
    )
