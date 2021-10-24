package com.example.gardenbotapp.data.remote.model

const val MAX_VENT_CYCLE = 15

data class VentilationCycleParams(val cycleOn: Float) {
    val cycleOnInteger: Int
        get() = (cycleOn * MAX_VENT_CYCLE).toInt()

    val cycleOffInteger: Int
        get() = MAX_VENT_CYCLE - cycleOnInteger

    companion object {
        fun fromInt(cycleOn: Int):VentilationCycleParams {
            return VentilationCycleParams((cycleOn / MAX_VENT_CYCLE).toFloat())
        }
    }
}