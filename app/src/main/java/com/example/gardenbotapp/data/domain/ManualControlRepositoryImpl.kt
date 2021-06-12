/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import android.util.Log
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.SendMqttOrderMutation
import com.example.gardenbotapp.data.remote.Client
import com.example.gardenbotapp.data.remote.GardenBotRepository
import com.example.gardenbotapp.type.Payload

class ManualControlRepositoryImpl : ManualControlRepository, GardenBotBaseRepositoryImpl() {

    override suspend fun sendMqttOrder(payload: Payload, token: String): String? {
        try {
            val response =
                Client.getInstance(token).mutate(SendMqttOrderMutation(Input.fromNullable(payload)))
                    .await()

            if (response.data == null || response.hasErrors()) {
                Log.i(GardenBotRepository.TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.sendMqttOrder
                }
            }
        } catch (e: ApolloException) {
            Log.i(GardenBotRepository.TAG, "ERROR: ${e.message}")
            throw e
        }
        return null
    }
}