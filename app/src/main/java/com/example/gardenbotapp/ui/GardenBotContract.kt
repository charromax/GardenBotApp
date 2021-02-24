package com.example.gardenbotapp.ui

import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.example.gardenbotapp.LoginUserMutation
import com.example.gardenbotapp.MeasuresQuery
import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.type.RegisterInput
import kotlinx.coroutines.flow.Flow

interface GardenBotContract {
    suspend fun getMeasuresForDevice(deviceId: String): Flow<List<MeasuresQuery.GetMeasure?>>

    suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register?
    suspend fun loginUser(username: String, password:String): LoginUserMutation.Login?
}