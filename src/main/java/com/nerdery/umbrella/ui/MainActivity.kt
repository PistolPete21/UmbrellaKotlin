package com.nerdery.umbrella.ui

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.nerdery.umbrella.R
import com.nerdery.umbrella.data.ApiServicesProvider
import com.nerdery.umbrella.data.ZipCodeService
import com.nerdery.umbrella.data.ZipLocation
import com.nerdery.umbrella.data.ZipLocationListener
import com.nerdery.umbrella.data.model.ForecastCondition
import com.nerdery.umbrella.data.model.HourlyResponse
import com.nerdery.umbrella.data.model.TempUnit
import com.nerdery.umbrella.data.model.WeatherResponse
import com.nerdery.umbrella.ui.adapter.MainAdapter
import com.nerdery.umbrella.util.DateTime
import com.nerdery.umbrella.widget.WeatherDayGridLayoutManager
import retrofit2.Call
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity(), ZipLocationListener {

    private var zipLocation: ZipLocation? = null
    private var hourlyResponse: HourlyResponse? = null
    private var currentForecast: ForecastCondition? = null
    private var tomorrowSublistIndex:Int = 0
    private var maxSublistIndex:Int = 0
    private var dayOfMonth:Int = 0
    private var integerDayOfMonth:Int = 0

    override fun onLocationFound(location: ZipLocation) {
        ApiServicesProvider(application)
                .weatherService.getWeatherCall(location.latitude, location.longitude, TempUnit.FAHRENHEIT)
                .enqueue(object : retrofit2.Callback<WeatherResponse> {
                    override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                        zipLocation = location
                        hourlyResponse = response.body()?.hourly
                        currentForecast = response.body()?.currentForecast
                        setupView(currentForecast, hourlyResponse)
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

    private fun setupView(forecastCondition: ForecastCondition?, hourlyResponse: HourlyResponse?) {
        // Make references to header views
        val cardView:CardView = findViewById(R.id.fragment_card_view_weather)
        val currentTemperature:TextView = findViewById(R.id.fragment_weather_degrees)
        val currentStatus:TextView = findViewById(R.id.fragment_weather_status)
        val currentLocation:TextView = findViewById(R.id.fragment_location_status)

        // Setup header
        currentLocation.text = String.format("%s, %s", zipLocation?.city?.toLowerCase()?.capitalize(), zipLocation?.state)
        currentTemperature.text = String.format("%s\u00B0", forecastCondition?.temp?.let { Math.round(it).toString() })
        currentStatus.text = forecastCondition?.summary
        if (forecastCondition != null) if (forecastCondition.temp >= 60) {
                cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.weather_warm))
            } else {
                cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.weather_cool))
            }

        // Setup forecast cards

        val linearLayout:LinearLayout = findViewById(R.id.fragment_linear_layout_hourly)
        linearLayout.removeAllViews()

        val items: List<ForecastCondition>? = hourlyResponse?.hours

        if (items != null) {
            for (item in items) {
                val cal = Calendar.getInstance()
                val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)

                val stringDayOfMonth:String = DateTime.convertDateToString(item.time, DateTime.dayFormatter)
                val integerDayOfMonth:Int = Integer.parseInt(stringDayOfMonth)

                if (integerDayOfMonth > dayOfMonth) {
                    tomorrowSublistIndex = items.indexOf(item)
                    break;
                }
            }
        }
//        if (items != null) {
//            for (item in items) {
//                if (integerDayOfMonth + 1 > dayOfMonth) {
//                    maxSublistIndex = items.indexOf(item)
//                    break;
//                }
//            }
//        }

        val subListToday: List<ForecastCondition>? = items?.subList(1, tomorrowSublistIndex);
        val subListTomorrow: List<ForecastCondition>? = items?.subList(tomorrowSublistIndex, items.size)

        for (i in 0..1) {
            // Instantiate the Views for inflating
            val cardHourlyForecast: View = LayoutInflater.from(this).inflate(R.layout.list_weather_forecast, linearLayout, false)

            // Make references to list_weather_recyclerview
            val todayTomorrow:TextView = cardHourlyForecast.findViewById(R.id.forecast_day)
            val recyclerView:RecyclerView = cardHourlyForecast.findViewById(R.id.fragment_weather_item)
            recyclerView.layoutManager = WeatherDayGridLayoutManager(this, WeatherDayGridLayoutManager.calculateNoOfColumns(this))
            //recyclerView.adapter = MainAdapter(items, this, application)

            if (i == 0) {
                todayTomorrow.text = getString(R.string.today)
                recyclerView.adapter = MainAdapter(subListToday, this, application)
                linearLayout.addView(cardHourlyForecast)
            } else {
                todayTomorrow.text = getString(R.string.tomorrow)
                recyclerView.adapter = MainAdapter(subListTomorrow, this, application)
                linearLayout.addView(cardHourlyForecast)
            }
        }
    }
}