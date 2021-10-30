/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.util

import kotlin.math.roundToInt

fun Float.convertToTemperature(): Int {
    return calculate(this, MIN_ALLOWED_TEMPERATURE, MAX_ALLOWED_TEMPERATURE)
}

private fun calculate(fl: Float, minValue: Int, maxValue: Int) =
    ((fl * (maxValue - minValue)) + minValue).roundToInt()

fun Float.convertToAirHumidityPercent(): Int {
    return calculate(this, MIN_ALLOWED_AIR_HUMIDITY, MAX_ALLOWED_AIR_HUMIDITY)
}


fun Float.convertToSoilHumidityPercent(): Int {
    return calculate(this, MIN_ALLOWED_SOIL_HUMIDITY, MAX_ALLOWED_SOIL_HUMIDITY)
}


fun Float.convertToVentilationCycle(): Int {
    return (this * MAX_VENT_CYCLE).roundToInt()
}
