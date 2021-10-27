package com.example.gardenbotapp.util

/**
 * extensions used to convert parameter values from the server to normalised floats to set the sliders
 */
fun Int.convertToAirTempFloat(): Float {
    return when {
        this < MIN_ALLOWED_TEMPERATURE -> calculate(MIN_ALLOWED_TEMPERATURE, MIN_ALLOWED_TEMPERATURE, MAX_ALLOWED_TEMPERATURE)
        this > MAX_ALLOWED_TEMPERATURE -> calculate(MAX_ALLOWED_TEMPERATURE, MIN_ALLOWED_TEMPERATURE, MAX_ALLOWED_TEMPERATURE)
        else -> calculate(this, MIN_ALLOWED_TEMPERATURE, MAX_ALLOWED_TEMPERATURE)
    }
}

fun Int.convertToAirHumFloat(): Float {
    return when {
        this < MIN_ALLOWED_AIR_HUMIDITY -> calculate(MIN_ALLOWED_AIR_HUMIDITY, MIN_ALLOWED_AIR_HUMIDITY, MAX_ALLOWED_AIR_HUMIDITY)
        this > MAX_ALLOWED_AIR_HUMIDITY -> calculate(MAX_ALLOWED_AIR_HUMIDITY, MIN_ALLOWED_AIR_HUMIDITY, MAX_ALLOWED_AIR_HUMIDITY)
        else -> calculate(this, MIN_ALLOWED_AIR_HUMIDITY, MAX_ALLOWED_AIR_HUMIDITY)
    }
}

fun Int.convertTosoilHumFloat(): Float {
    return when {
        this < MIN_ALLOWED_SOIL_HUMIDITY -> calculate(MIN_ALLOWED_SOIL_HUMIDITY, MIN_ALLOWED_SOIL_HUMIDITY, MAX_ALLOWED_TEMPERATURE)
        this > MAX_ALLOWED_SOIL_HUMIDITY -> calculate(MAX_ALLOWED_SOIL_HUMIDITY, MIN_ALLOWED_SOIL_HUMIDITY, MAX_ALLOWED_SOIL_HUMIDITY)
        else -> calculate(this, MIN_ALLOWED_SOIL_HUMIDITY, MAX_ALLOWED_SOIL_HUMIDITY)
    }
}

private fun calculate(value: Int, minValue: Int, maxValue: Int) =
    (value.toFloat() - minValue) / (maxValue - minValue)

fun Int.convertToVentilationFloat():Float {
    return this.toFloat() / MAX_VENT_CYCLE
}

fun Int.getVentilationCycleOff():Int {
    return MAX_VENT_CYCLE - this
}