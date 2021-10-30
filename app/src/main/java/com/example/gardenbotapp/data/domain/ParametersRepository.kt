/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.apollographql.apollo.api.Response
import com.example.gardenbotapp.NewAutoPilotParamsResponseSubscription
import com.example.gardenbotapp.type.ParamsPayload
import com.example.gardenbotapp.type.StatusRequest
import kotlinx.coroutines.flow.Flow

interface ParametersRepository : GardenBotRepository {
    suspend fun sendParamsStatusRequest(statusRequest: StatusRequest, token: String): String?
    suspend fun changeParams(payload: ParamsPayload, token: String): String?
    suspend fun paramsStatusResponseSub(deviceId: String): Flow<Response<NewAutoPilotParamsResponseSubscription.Data>>
}