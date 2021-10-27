package com.example.gardenbotapp.data.model

import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.type.Params
import com.example.gardenbotapp.type.ParamsPayload
import kotlinx.coroutines.flow.first

data class AutoPilotParamsPayload(
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
