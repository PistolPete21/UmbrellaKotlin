package com.nerdery.umbrella.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.LinearLayout
import android.widget.TextView
import com.nerdery.umbrella.R
import com.nerdery.umbrella.data.ZipLocation
import com.nerdery.umbrella.data.model.TempUnit
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import com.yarolegovich.lovelydialog.LovelyTextInputDialog

class SettingsActivity : AppCompatActivity() {

    private var location: ZipLocation? = null
    private var tempUnit: TempUnit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (intent != null) {
            //location = this.intent.getSerializableExtra("ZipLocation") as ZipLocation
            //tempUnit = this.intent.getSerializableExtra("TempUnit") as TempUnit
        }
        setupView(location)
    }

    private fun setupView(location: ZipLocation?) {
        //setup toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.umbrella_toolbar_text)
        (this as AppCompatActivity).setSupportActionBar(toolbar)
        (this as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //setup view
        val zipCodeLayout: LinearLayout = findViewById(R.id.first_zip_code_layout)
        val unitsLayout: LinearLayout = findViewById(R.id.units_layout)
        val zip:TextView = findViewById(R.id.zip_value)
        val units:TextView = findViewById(R.id.units_value)

        zip.text = location?.zipCode.toString()
        if (tempUnit == TempUnit.FAHRENHEIT) {
            units.text = getString(R.string.fahrenheit)
        } else {
            units.text = getString(R.string.celsius)
        }

        zipCodeLayout.setOnClickListener {
            LovelyTextInputDialog(this, R.style.Theme_Umbrella)
                    .setTopColorRes(R.color.weather_cool)
                    .setTitle(R.string.zip)
                    .setMessage(R.string.zip_message)
                    .setIcon(R.mipmap.ic_launcher)
                    .setInputFilter(R.string.zip_error) { text ->
                        if (text.length > 5 || text.length < 5) {
                            false
                        } else {
                            text.matches(("\\w+[0-9]").toRegex())
                        }
                    }
                    .setConfirmButton(android.R.string.ok) { text -> zip.text = text }
                    .show()
        }

        unitsLayout.setOnClickListener {
            val unitNames = resources.getStringArray(R.array.units)
            LovelyChoiceDialog(this)
                    .setTopColorRes(R.color.weather_cool)
                    .setTitle(R.string.units)
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(R.string.unit_message)
                    .setItems(unitNames) { position, item ->
                        if (position == 0) {
                            units.text = getString(R.string.fahrenheit)
                            tempUnit = TempUnit.FAHRENHEIT
                        } else if (position == 1) {
                            units.text = getString(R.string.celsius)
                            tempUnit = TempUnit.CELSIUS
                        }
                    }
                    .show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        //intent.putExtra("ZipLocation", location)
        //intent.putExtra("TempUnit", tempUnit)
        setResult(RESULT_OK, intent);
    }
}

