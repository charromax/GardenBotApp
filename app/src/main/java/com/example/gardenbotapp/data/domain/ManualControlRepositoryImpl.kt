/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import android.util.Log
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.NewStatusResponseSubscription
import com.example.gardenbotapp.SendMqttOrderMutation
import com.example.gardenbotapp.StatusRequestMutation
import com.example.gardenbotapp.data.remote.Client
import com.example.gardenbotapp.type.Payload
import com.example.gardenbotapp.type.StatusRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class ManualControlRepositoryImpl : ManualControlRepository, GardenBotRepositoryImpl() {

    companion object {
        const val TAG = "MANUAL_CONTROL_REPO"
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

    override suspend fun sendDeviceStatusRequst(
        statusRequest: StatusRequest,
        token: String
    ): String? {
        try {
            val response =
                Client.getInstance(token).mutate(StatusRequestMutation(statusRequest)).await()
            if (response.data == null || response.hasErrors()) {
                Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.statusRequest
                }
            }
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw e
        }
        return null
    }

    override suspend fun statusResponseSub(deviceId: String): Flow<Response<NewStatusResponseSubscription.Data>> {
        try {
            return Client.getInstance().subscribe(NewStatusResponseSubscription(deviceId)).toFlow()
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw ApolloException("${e.message}")
        }
    }
}