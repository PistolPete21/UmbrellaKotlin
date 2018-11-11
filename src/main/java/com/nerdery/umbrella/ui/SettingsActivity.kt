package com.nerdery.umbrella.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.google.gson.Gson
import com.nerdery.umbrella.R
import com.nerdery.umbrella.data.ZipLocation

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val jsonObject:String? = savedInstanceState?.getString("ZipLocation")
        val location:ZipLocation = Gson().fromJson(jsonObject, ZipLocation::class.java)
        setupView(location)
    }

    private fun setupView(location:ZipLocation) {
        //setup toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.umbrella_toolbar_text)
        (this as AppCompatActivity).setSupportActionBar(toolbar)
        (this as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //setup view
        val zip:TextView = findViewById(R.id.zip_value)
        val units:TextView = findViewById(R.id.units_value)

        zip.text = ZipLocation().zipCode.toString()
    }
}

