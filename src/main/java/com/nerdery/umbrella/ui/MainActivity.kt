package com.nerdery.umbrella.ui

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.util.Log
import android.view.Menu
import android.widget.Adapter
import android.widget.TextView
import com.nerdery.umbrella.R
import com.nerdery.umbrella.data.ApiServicesProvider
import com.nerdery.umbrella.data.ZipCodeService
import com.nerdery.umbrella.data.ZipLocation
import com.nerdery.umbrella.data.ZipLocationListener
import com.nerdery.umbrella.data.model.ForecastCondition
import com.nerdery.umbrella.data.model.TempUnit
import com.nerdery.umbrella.data.model.WeatherResponse
import com.nerdery.umbrella.ui.adapter.MainAdapter

import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity(), ZipLocationListener {

    private var zipLocation: ZipLocation? = null

    override fun onLocationFound(location: ZipLocation) {
        ApiServicesProvider(application)
                .weatherService.getWeatherCall(location.latitude, location.longitude, TempUnit.FAHRENHEIT)
                .enqueue(object : retrofit2.Callback<WeatherResponse> {
                    override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                        zipLocation = location
                        response.body()?.currentForecast?.let { setupView(it) }
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        Log.e("Weather", t.message)
                    }

                })
    }

    override fun onLocationNotFound() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupProgressDialog()

        ZipCodeService.getLatLongByZip(this, "60647", this)
    }

    private fun setupProgressDialog() {
        //
    }

    private fun setupView(forecastCondition: ForecastCondition) {
        val cardView:CardView = findViewById(R.id.fragment_card_view_weather)
        val currentTemperature:TextView = findViewById(R.id.fragment_weather_degrees)
        val currentStatus:TextView = findViewById(R.id.fragment_weather_status)
        val currentLocation:TextView = findViewById(R.id.fragment_location_status)

        currentLocation.text = String.format("%s, %s", zipLocation?.city?.toLowerCase()?.capitalize(), zipLocation?.state)
        currentTemperature.text = String.format("%s\u00B0", Math.round(forecastCondition.temp).toString())
        currentStatus.text = forecastCondition.summary
        if (forecastCondition.temp >= 60) {
            cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.weather_warm))
        } else {
            cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.weather_cool))
        }

        MainAdapter(this)
    }
}
