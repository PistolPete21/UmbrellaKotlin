package com.nerdery.umbrella.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.nerdery.umbrella.R
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * This Service is used to lookup location details of a specific US ZIP code
 */
object ZipCodeService {

    /**
     * Request location details of a given zip code
     * @param context Generic context to retrieve zip codes resources
     * @param zipCode Numerical zip code
     * @param listener ZipLocationListener used to listen for successful result or error
     */
    fun getLatLongByZip(context: Context, zipCode: String, listener: ZipLocationListener) {
        val zipLong = zipCode.toLongOrNull()

        if (zipLong == null) {
            listener.onLocationNotFound()
            return
        }

        val stream = context.resources.openRawResource(R.raw.zipcodes)
        val jsonString = readJsonFile(stream)
        val locations = Gson().fromJson<Array<ZipLocation>>(
                jsonString,
                Array<ZipLocation>::class.java
        )

        for (location in locations) {
            if (location.zipCode == zipLong) {
                listener.onLocationFound(location)
                return
            }
        }

        listener.onLocationNotFound()
    }

    fun readJsonFile(inputStream: InputStream): String? {
        val outputStream = ByteArrayOutputStream()

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return outputStream.toString()
    }

}

interface ZipLocationListener {
    fun onLocationFound(location: ZipLocation)
    fun onLocationNotFound()
}

class ZipLocation {
    @SerializedName("z")
    var zipCode: Long = 0
        internal set

    @SerializedName("c")
    var city: String = ""
        internal set

    @SerializedName("s")
    var state: String = ""
        internal set

    @SerializedName("la")
    var latitude: Double = 0.0
        internal set

    @SerializedName("lo")
    var longitude: Double = 0.0
        internal set
}