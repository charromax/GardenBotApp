/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data

import com.apollographql.apollo.api.Response
import com.example.gardenbotapp.*
import com.example.gardenbotapp.data.model.Measure
import com.example.gardenbotapp.type.Payload
import com.example.gardenbotapp.type.RegisterInput
import kotlinx.coroutines.flow.Flow

interface GardenBotContract {
    suspend fun getMeasuresForDevice(
        deviceId: String,
        token: String
    ): Flow<List<Measure>>

    suspend fun newMeasureSub(deviceId: String): Flow<Response<NewMeasureSubscription.Data>>

    suspend fun newDeviceSub(deviceName: String): Flow<Response<NewDeviceSubscription.Data>>

    suspend fun activateDevice(deviceName: String, userId: String, token: String): String

    suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register?

    suspend fun loginUser(username: String, password: String): LoginUserMutation.Login?

    suspend fun refreshToken(token: String): RefreshTokenQuery.Data?

    suspend fun sendMqttOrder(payload: Payload, token: String): String?
}