/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.util

import kotlin.math.roundToInt

/**
 * extensions used to convert normalized float values from sliders into server-ready values
 */
fun Float.shorten(decimals: Int): Float {
    return String.format("%.$decimals" + "f", this).replace(',', '.').toFloat()
}

fun Float.convertToTemperature(): Float {
    return calculate(this, MIN_ALLOWED_TEMPERATURE, MAX_ALLOWED_TEMPERATURE)
}

private fun calculate(fl: Float, minValue: Int, maxValue: Int) =
    (fl * (maxValue - minValue)) + minValue

fun Float.convertToAirHumidityPercent(): Float {
    return calculate(this, MIN_ALLOWED_AIR_HUMIDITY, MAX_ALLOWED_AIR_HUMIDITY)
}


fun Float.convertToSoilHumidityPercent(): Float {
    return calculate(this, MIN_ALLOWED_SOIL_HUMIDITY, MAX_ALLOWED_SOIL_HUMIDITY)
}


fun Float.convertToVentilationCycle(): Int {
    return (this * MAX_VENT_CYCLE).roundToInt()
}
