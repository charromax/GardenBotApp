/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.apollographql.apollo.api.Response
import com.example.gardenbotapp.NewStatusResponseSubscription
import com.example.gardenbotapp.type.Payload
import com.example.gardenbotapp.type.StatusRequest
import kotlinx.coroutines.flow.Flow

interface ManualControlRepository : GardenBotRepository {
    suspend fun sendMqttOrder(payload: Payload, token: String): String?
    suspend fun sendDeviceStatusRequst(statusRequest: StatusRequest, token: String): String?
    suspend fun statusResponseSub(deviceId: String): Flow<Response<NewStatusResponseSubscription.Data>>
}