/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.model

import com.anychart.chart.common.dataentry.ValueDataEntry

class ChartModel(
    x: String,
    airTemp: Number,
    airHum: Number,
    soilHum: Number
) :
    ValueDataEntry(x, airTemp) {
    init {
        setValue("airHum", airHum)
        setValue("soilHum", soilHum)
    }
}