package com.nerdery.umbrella.widget

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View


class WeatherDayGridLayoutManager : GridLayoutManager {

    constructor(context: Context, spanCount: Int) : super(context, spanCount)

    constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean) : super(context, spanCount, orientation, reverseLayout)

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        val widthSize = View.MeasureSpec.getSize(widthSpec)
        val rows = Math.ceil((itemCount / spanCount).toDouble()).toInt() // Width of the RecyclerView

        setMeasuredDimension(widthSize, rows * WEATHER_CELL_HEIGHT)
    }

    companion object {
        private const val WEATHER_CELL_HEIGHT = 100

        fun calculateNoOfColumns(context: Context): Int {
            val displayMetrics = context.resources.displayMetrics
            val dpWidth = displayMetrics.widthPixels / displayMetrics.density
            return (dpWidth / 100).toInt()
        }
    }
}
