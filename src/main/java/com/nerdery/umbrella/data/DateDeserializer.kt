package com.nerdery.umbrella.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.util.*

class DateDeserializer : JsonDeserializer<Date> {

    override fun deserialize(json: JsonElement?, typeOfT: Type, context: JsonDeserializationContext): Date? {
        if (json == null) return null
        return Date(json.asLong * 1000)
    }

}