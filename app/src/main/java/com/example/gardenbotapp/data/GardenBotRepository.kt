/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data

import com.example.gardenbotapp.LoginUserMutation
import com.example.gardenbotapp.MeasuresQuery
import com.example.gardenbotapp.RefreshTokenQuery
import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.data.remote.Client
import com.example.gardenbotapp.type.RegisterInput
import com.example.gardenbotapp.ui.GardenBotContract
import kotlinx.coroutines.flow.Flow

class GardenBotRepository: GardenBotContract {


    override suspend fun getMeasuresForDevice(
        deviceId: String,
        token: String
    ): Flow<List<MeasuresQuery.GetMeasure?>> {
        return Client(token).getAllMeasures(deviceId)
    }


    override suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register? {
        return Client(null).registerNewUser(userInput)
    }

    override suspend fun loginUser(username: String, password: String): LoginUserMutation.Login? {
        return try {
            Client(null).loginUser(username, password)
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override suspend fun refreshToken(token: String): RefreshTokenQuery.Data? {
        return Client(token).refreshToken()
    }
}