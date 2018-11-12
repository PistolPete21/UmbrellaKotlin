package com.nerdery.umbrella.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Temperature unit to be used in requests for [com.nerdery.umbrella.data.api.WeatherService]
 */
enum class TempUnit(private val value: String) : Serializable {
    @SerializedName("si")
    CELSIUS("si"),

    @SerializedName("us")
    FAHRENHEIT("us");

    override fun toString(): String {
        return value
    }
}
