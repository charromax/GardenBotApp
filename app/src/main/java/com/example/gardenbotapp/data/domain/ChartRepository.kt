/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.apollographql.apollo.api.Response
import com.example.gardenbotapp.NewMeasureSubscription
import com.example.gardenbotapp.data.model.Measure
import kotlinx.coroutines.flow.Flow

interface ChartRepository : GardenBotRepository {
    suspend fun getMeasuresForDevice(
        deviceId: String,
        token: String
    ): Flow<List<Measure>>

    suspend fun newMeasureSub(deviceId: String): Flow<Response<NewMeasureSubscription.Data>>
}