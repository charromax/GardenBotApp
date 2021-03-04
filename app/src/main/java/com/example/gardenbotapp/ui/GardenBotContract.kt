/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui

import com.example.gardenbotapp.LoginUserMutation
import com.example.gardenbotapp.MeasuresQuery
import com.example.gardenbotapp.RefreshTokenQuery
import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.type.RegisterInput
import kotlinx.coroutines.flow.Flow

interface GardenBotContract {
    suspend fun getMeasuresForDevice(
        deviceId: String,
        token: String
    ): Flow<List<MeasuresQuery.GetMeasure?>>

    suspend fun activateDevice(deviceName: String, userId: String, token: String): String

    suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register?
    suspend fun loginUser(username: String, password: String): LoginUserMutation.Login?
    suspend fun refreshToken(token: String): RefreshTokenQuery.Data?
}