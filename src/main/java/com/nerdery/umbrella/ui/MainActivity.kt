package com.nerdery.umbrella.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.nerdery.umbrella.R
import com.nerdery.umbrella.data.ApiServicesProvider
import com.nerdery.umbrella.data.ZipCodeService
import com.nerdery.umbrella.data.ZipLocation
import com.nerdery.umbrella.data.ZipLocationListener
import com.nerdery.umbrella.data.model.TempUnit
import io.reactivex.android.schedulers.AndroidSchedulers

class MainActivity : AppCompatActivity(), ZipLocationListener {

    override fun onLocationFound(location: ZipLocation) {
        ApiServicesProvider(application)
                .weatherService
                .getWeather(location.latitude, location.longitude, TempUnit.FAHRENHEIT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { success ->
                            Log.d("Weather", success.currentForecast?.summary)
                        },
                        { error ->
                            //Log.e("Weather", error.message)
                        }
                )
    }

    override fun onLocationNotFound() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ZipCodeService.getLatLongByZip(this, "60647", this)
    }
}