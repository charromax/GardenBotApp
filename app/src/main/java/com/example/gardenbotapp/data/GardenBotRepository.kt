package com.example.gardenbotapp.data

import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.example.gardenbotapp.LoginUserMutation
import com.example.gardenbotapp.MeasuresQuery
import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.data.remote.Client
import com.example.gardenbotapp.type.RegisterInput
import com.example.gardenbotapp.ui.GardenBotContract
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.lang.Exception

class GardenBotRepository: GardenBotContract {

    @ExperimentalCoroutinesApi
    override suspend fun getMeasuresForDevice(deviceId: String): Flow<List<MeasuresQuery.GetMeasure?>> {
        return Client.getAllMeasures(deviceId)
    }

    override suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register? {
        return Client.registerNewUser(userInput)
    }

    override suspend fun loginUser(username: String, password: String): LoginUserMutation.Login? {
        return try { Client.loginUser(username, password) } catch (e:Exception) { throw Exception(e.message) }
    }
}