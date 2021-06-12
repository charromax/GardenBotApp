/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import android.util.Log
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.MeasuresQuery
import com.example.gardenbotapp.NewMeasureSubscription
import com.example.gardenbotapp.data.remote.Client
import com.example.gardenbotapp.data.remote.GardenBotRepository
import com.example.gardenbotapp.data.remote.model.Measure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

class ChartRepositoryImpl : ChartRepository, GardenBotBaseRepositoryImpl() {

    override suspend fun newMeasureSub(deviceId: String): Flow<Response<NewMeasureSubscription.Data>> {
        try {
            return Client.getInstance().subscribe(NewMeasureSubscription(deviceId)).toFlow()
        } catch (e: ApolloException) {
            Log.i(GardenBotRepository.TAG, "ERROR: ${e.message}")
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
                    Log.i(GardenBotRepository.TAG, "ERROR: ${response.errors?.map { it.message }}")
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
                Log.i(GardenBotRepository.TAG, "ERROR: ${e.message}")
                throw ApolloException("${e.message}")
            }
        }
        return emptyFlow()
    }
}