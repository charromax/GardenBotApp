/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.util

import com.example.gardenbotapp.ui.home.sections.chart.*

fun Float.shorten(decimals: Int): Float {
    return String.format("%.$decimals" + "f", this).replace(',', '.').toFloat()
}

fun Float.convertToTemperature(): Float {
    return MIN_ALLOWED_TEMPERATURE + (this * (MAX_ALLOWED_TEMPERATURE - MIN_ALLOWED_TEMPERATURE))
}

fun Float.convertToAirHumidityPercent(): Float {
    return MIN_ALLOWED_AIR_HUMIDITY + (this * (MAX_ALLOWED_AIR_HUMIDITY - MIN_ALLOWED_AIR_HUMIDITY))
}

fun Float.convertToSoilHumidityPercent(): Float {
    return MIN_ALLOWED_SOIL_HUMIDITY + (this * (MAX_ALLOWED_SOIL_HUMIDITY - MIN_ALLOWED_SOIL_HUMIDITY))
}