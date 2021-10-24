package com.example.gardenbotapp.data.remote.model

data class VentilationCycleParams(val cycleOn: Float) {
    val cycleOnInteger: Int
        get() = (cycleOn * 15).toInt()

    val cycleOffInteger: Int
        get() = 15 - cycleOnInteger

    companion object {
        fun fromInt(cycleOn: Int):VentilationCycleParams {
            return VentilationCycleParams((cycleOn / 15).toFloat())
        }
    }
}