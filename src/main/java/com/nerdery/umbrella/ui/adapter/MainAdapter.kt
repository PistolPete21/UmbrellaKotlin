package com.nerdery.umbrella.ui.adapter

import android.app.Application
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.graphics.drawable.DrawableCompat.*
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nerdery.umbrella.R
import com.nerdery.umbrella.data.ApiServicesProvider
import com.nerdery.umbrella.data.IconProvider
import com.nerdery.umbrella.data.api.IconApi
import com.nerdery.umbrella.data.model.ForecastCondition
import com.nerdery.umbrella.ui.MainActivity
import com.nerdery.umbrella.util.DateTime
import kotlinx.android.synthetic.main.weather_grid_item.view.*
import java.security.AccessController.getContext
import kotlin.math.roundToInt

class MainAdapter(private val items: List<ForecastCondition>?, private val context: Context, private val application: Application) : RecyclerView.Adapter<MainAdapter.HourlyForecastHolder>() {
    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.weather_grid_item, parent, false)
        return HourlyForecastHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyForecastHolder, position: Int) {
        val timeString:String = DateTime.convertDateToString(items?.get(position)?.time, DateTime.timeFormatter)
        holder.time.text = timeString
        holder.temperature.text = String.format("%s\u00B0", items!![position].temp.let { Math.round(it).toString() })

        val highlighted:IconProvider.IconType
        val maxTemp = items.maxBy { it -> it.temp }
        val minTemp = items.minBy { it -> it.temp }
        highlighted = if (items[position].temp == maxTemp?.temp || items[position].temp == minTemp?.temp) {
            IconProvider.IconType.HIGHLIGHTED
        } else {
            IconProvider.IconType.NORMAL
        }
        ApiServicesProvider(application).picasso
                .load(items[position].icon?.let { IconProvider().getUrlForIcon(it, highlighted)})
                .error(R.drawable.ic_error_icon)
                .into(holder.icon)

        if (items[position].temp == maxTemp?.temp) {
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.weather_warm))
            holder.temperature.setTextColor(ContextCompat.getColor(context, R.color.weather_warm))
            holder.icon.setColorFilter(ContextCompat.getColor(context, R.color.weather_warm), android.graphics.PorterDuff.Mode.SRC_IN)
        } else if (items[position].temp == minTemp?.temp) {
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.weather_cool))
            holder.temperature.setTextColor(ContextCompat.getColor(context, R.color.weather_cool))
            holder.icon.setColorFilter(ContextCompat.getColor(context, R.color.weather_cool), android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            holder.icon.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }

    class HourlyForecastHolder(v: View) : RecyclerView.ViewHolder(v) {
        val time: TextView = v.grid_item_hourly_hour
        val icon: ImageView = v.grid_item_hourly_icon
        val temperature: TextView = v.grid_item_hourly_temperature
    }

}
