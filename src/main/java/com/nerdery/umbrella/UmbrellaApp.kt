package com.nerdery.umbrella

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class UmbrellaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

}