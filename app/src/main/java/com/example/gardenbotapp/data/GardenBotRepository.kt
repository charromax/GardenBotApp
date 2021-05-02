/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data

import android.util.Log
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.*
import com.example.gardenbotapp.data.model.Measure
import com.example.gardenbotapp.type.Payload
import com.example.gardenbotapp.type.RegisterInput
import com.example.gardenbotapp.ui.GardenBotContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

class GardenBotRepository : GardenBotContract {
    companion object {
        const val TAG = "REPO"
    }

    override suspend fun newMeasureSub(deviceId: String): Flow<Response<NewMeasureSubscription.Data>> {
        try {
            return Client.getInstance().subscribe(NewMeasureSubscription(deviceId)).toFlow()
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw ApolloException("${e.message}")
        }
    }

    override suspend fun newDeviceSub(deviceName: String): Flow<Response<NewDeviceSubscription.Data>> {
        try {
            return Client.getInstance().subscribe(NewDeviceSubscription(deviceName)).toFlow()
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw ApolloException("${e.message}")
        }
    }


    override suspend fun getMeasuresForDevice(
        deviceId: String,
        token: String
    ): Flow<List<Measure>> {
        if (deviceId.isNotBlank()) {
            try {
                val response = Client.getInstance(token).query(MeasuresQuery(deviceId)).await()
                if (response.data?.getMeasures == null || response.hasErrors()) {
                    Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                    throw ApolloException("${response.errors?.map { it.message }}")
                } else {
                    response.data?.let {
                        return flowOf(it.getMeasures.map { data ->
                            Measure(
                                id = data?.id ?: "",
                                airHum = data?.airHum ?: 0.0,
                                airTemp = data?.airTemp ?: 0.0,
                                soilHum = data?.soilHum ?: 0.0,
                                createdAt = data?.createdAt ?: ""
                            )
                        })
                    }
                }
            } catch (e: ApolloException) {
                Log.i(TAG, "ERROR: ${e.message}")
                throw ApolloException("${e.message}")
            }
        }
        return emptyFlow()
    }

    override suspend fun activateDevice(
        deviceName: String,
        userId: String,
        token: String
    ): String {
        if (deviceName.isNotEmpty()) {
            try {
                val response =
                    Client.getInstance(token).mutate(ActivateDeviceMutation(deviceName, userId))
                        .await()
                if (response.data?.activateDevice == null || response.hasErrors()) {
                    Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                    throw ApolloException("${response.errors?.map { it.message }}")
                } else {
                    response.data?.let {
                        return it.activateDevice.id
                    }
                }
            } catch (e: ApolloException) {
                Log.i(TAG, "ERROR: ${e.message}")
                throw ApolloException("${e.message}")
            }
        }
        return ""
    }


    override suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register? {
        try {
            val response = Client.getInstance().mutate(RegisterUserMutation(userInput)).await()
            if (response.data?.register == null || response.hasErrors()) {
                Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.register
                }
            }

        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw ApolloException("${e.message}")
        }
        return null
    }

    override suspend fun loginUser(username: String, password: String): LoginUserMutation.Login? {
        try {
            val response =
                Client.getInstance().mutate(LoginUserMutation(username, password)).await()

            if (response.data?.login == null || response.hasErrors()) {
                Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.login
                }
            }
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.printStackTrace()}")
            throw ApolloException("${e.message}")
        }
        return null
    }

    override suspend fun refreshToken(token: String): RefreshTokenQuery.Data? {
        try {
            val response = Client.getInstance(token).query(RefreshTokenQuery()).await()

            if (response.data == null || response.hasErrors()) {
                Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it
                }
            }
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw e
        }
        return null
    }

    override suspend fun sendMqttOrder(payload: Payload, token: String): String? {
        try {
            val response =
                Client.getInstance(token).mutate(SendMqttOrderMutation(Input.fromNullable(payload)))
                    .await()

            if (response.data == null || response.hasErrors()) {
                Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.sendMqttOrder
                }
            }
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw e
        }
        return null

    }
}