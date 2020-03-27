package com.nerdery.umbrella.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
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
import com.yarolegovich.lovelydialog.LovelyProgressDialog
import retrofit2.Call
import retrofit2.Response
import java.util.*






class MainActivity : AppCompatActivity(), ZipLocationListener {

    private var hourlyResponse: HourlyResponse? = null
    private var currentForecast: ForecastCondition? = null
    private var zipLocation: ZipLocation? = null
    private var zipString: String = "55431"
    private var tempUnit:TempUnit = TempUnit.FAHRENHEIT
    private var tomorrowSublistIndex:Int = 0
    private var maxSublistIndex:Int = 0

    private var lovelyProgressDialog: LovelyProgressDialog? = null

    override fun onLocationFound(location: ZipLocation) {
        ApiServicesProvider(application)
                .weatherService.getWeatherCall(location.latitude, location.longitude, tempUnit)
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
        ZipCodeService.getLatLongByZip(this, zipString, this)
    }

    @Suppress("DEPRECATION")
    private fun setupProgressDialog() {
        lovelyProgressDialog = LovelyProgressDialog(this)
        lovelyProgressDialog?.setIcon(R.mipmap.ic_launcher)
        lovelyProgressDialog?.setTitle(R.string.fetching_weather)
        lovelyProgressDialog?.setTopColor(resources.getColor(R.color.weather_cool))
        lovelyProgressDialog?.show()
    }

    private fun setupView(forecastCondition: ForecastCondition?, hourlyResponse: HourlyResponse?) {
        // Make references to header views
        val cardView: androidx.cardview.widget.CardView = findViewById(R.id.fragment_card_view_weather)
        val currentTemperature:TextView = findViewById(R.id.fragment_weather_degrees)
        val currentStatus:TextView = findViewById(R.id.fragment_weather_status)
        val currentLocation:TextView = findViewById(R.id.fragment_location_status)
        val settingsButton:ImageView = findViewById(R.id.fragment_image_button)

        // Setup header
        currentLocation.text = String.format("%s, %s", zipLocation?.city?.toLowerCase()?.capitalize(), zipLocation?.state)
        currentTemperature.text = String.format("%s\u00B0", forecastCondition?.temp?.let { Math.round(it).toString() })
        currentStatus.text = forecastCondition?.summary
        if (forecastCondition != null) if (forecastCondition.temp >= 60) {
                cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.weather_warm))
            } else {
                cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.weather_cool))
            }
        settingsButton.setOnClickListener {
            val gson = Gson()
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("ZipLocation", gson.toJson(zipLocation))
            intent.putExtra("TempUnit", gson.toJson(tempUnit))
            startActivityForResult(intent, 1)
        }

        // Setup forecast cards
        val linearLayout:LinearLayout = findViewById(R.id.fragment_linear_layout_hourly)
        linearLayout.removeAllViews()

        val items: List<ForecastCondition>? = hourlyResponse?.hours
        getTomorrowSublistIndex(items)
        getMaxSublistIndex(items)
        val subListToday: List<ForecastCondition>? = items?.subList(1, tomorrowSublistIndex);
        val subListTomorrow: List<ForecastCondition>? = items?.subList(tomorrowSublistIndex, 48)

        for (i in 0..1) {
            // Instantiate the Views for inflating
            val cardHourlyForecast: View = LayoutInflater.from(this).inflate(R.layout.list_weather_forecast, linearLayout, false)

            // Make references to list_weather_recyclerview
            val todayTomorrow:TextView = cardHourlyForecast.findViewById(R.id.forecast_day)
            val recyclerView: androidx.recyclerview.widget.RecyclerView = cardHourlyForecast.findViewById(R.id.fragment_weather_item)
            recyclerView.layoutManager = WeatherDayGridLayoutManager(this, WeatherDayGridLayoutManager.calculateNoOfColumns(this))

            if (i == 0) {
                todayTomorrow.text = getString(R.string.today)
                recyclerView.adapter = MainAdapter(subListToday, this, application, null)
                linearLayout.addView(cardHourlyForecast)
            } else {
                todayTomorrow.text = getString(R.string.tomorrow)
                recyclerView.adapter = lovelyProgressDialog?.let { MainAdapter(subListTomorrow, this, application, it) }
                linearLayout.addView(cardHourlyForecast)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val gson = Gson()
                val strObj = data?.getStringExtra("ZipLocation")
                val tempObj = data?.getStringExtra("TempUnit")
                zipString = gson.fromJson<String>(strObj, String::class.java)
                tempUnit = gson.fromJson<TempUnit>(tempObj, TempUnit::class.java)

                // Execute a new call
                ZipCodeService.getLatLongByZip(this, zipString, this)
                setupProgressDialog()
            }
        }
    }

    private fun getTomorrowSublistIndex(items:List<ForecastCondition>?) {
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
    }

    private fun getMaxSublistIndex(items:List<ForecastCondition>?) {
        if (items != null) {
            for (item in items) {
                val cal = Calendar.getInstance()
                val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)

                val stringDayOfMonth:String = DateTime.convertDateToString(item.time, DateTime.dayFormatter)
                val integerDayOfMonth:Int = Integer.parseInt(stringDayOfMonth)

                if (integerDayOfMonth > dayOfMonth + 1) {
                    maxSublistIndex = items.indexOf(item)
                    break;
                }
            }
        }
    }
}