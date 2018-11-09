package com.nerdery.umbrella.ui.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nerdery.umbrella.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_weather_forecast.view.*

class MainAdapter(val items : ArrayList<String>, val context: Context) : RecyclerView.Adapter<MainAdapter.HourlyForecastHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: HourlyForecastHolder, position: Int) {
        val hourlyForecast = items[position]
        holder.bindForecast(hourlyForecast)
    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastHolder {
//        /* val inflatedView = parent.inflate(R.layout.list_weather_forecast, false) */
//    }

    class HourlyForecastHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var time: String? = null
        private var temperature: String? = null

        fun bindForecast(item: String) {
            //Picasso.with(view.context).load(photo.url).into(view.itemImage)
        }
    }
}
