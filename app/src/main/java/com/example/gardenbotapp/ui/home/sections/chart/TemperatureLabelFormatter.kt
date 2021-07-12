/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.gardenbotapp.util.formatTo
import com.example.gardenbotapp.util.toDate
import com.example.gardenbotapp.util.toTemperatureString
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class TemperatureLabelFormatter : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val format = DecimalFormat("###,##0.0")
        return format.format(value).toTemperatureString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getPointLabel(entry: Entry?): String {
        val format = DecimalFormat("###,##0.0")
        return format.format(entry?.y).toTemperatureString()
    }
}

class DateLabelFormatter : ValueFormatter() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getPointLabel(entry: Entry?): String {
        return (entry?.data as String).toDate().formatTo("EEE, HH:mm")
    }
}

class NoLabelFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return ""
    }
}