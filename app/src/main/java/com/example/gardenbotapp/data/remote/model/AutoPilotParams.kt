package com.example.gardenbotapp.data.remote.model

import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.type.Params
import com.example.gardenbotapp.type.ParamsPayload
import kotlinx.coroutines.flow.first

data class AutoPilotParams(
    val type: String,
    val order: Params
) {
    suspend fun toParamsPayload(preferencesManager: PreferencesManager): ParamsPayload {
        return ParamsPayload(
            preferencesManager.deviceIdFlow.first(),
            type, order
        )
    }
}
