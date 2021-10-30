package com.example.gardenbotapp.data.model

import com.example.gardenbotapp.NewAutoPilotParamsResponseSubscription
import com.example.gardenbotapp.type.Params

data class Parameters(
    var auto_pilot_mode: String = "",
    var min_hum: Int = 0,
    var max_hum: Int = 0,
    var min_soil: Int = 0,
    var max_soil: Int = 0,
    var min_temp: Int = 0,
    var max_temp: Int = 0,
    var hour_on: Int = 0,
    var hour_off: Int = 0,
    var cycle_on: Int = 0,
    var cycle_off: Int = 0
) {
    fun toParams(): Params {
        return Params(
            auto_pilot_mode, min_hum, max_hum, min_soil, max_soil, min_temp, max_temp, hour_on, hour_off, cycle_on, cycle_off
        )
    }

    companion object {
        fun fromParams(data: NewAutoPilotParamsResponseSubscription.Params):Parameters {
            return Parameters(
                data.auto_pilot_mode,
                data.min_hum,
                data.max_hum,
                data.min_soil,
                data.max_soil,
                data.min_temp,
                data.max_temp,
                data.hour_on,
                data.hour_off,
                data.cycle_on,
                data.cycle_off
            )

        }
    }
}