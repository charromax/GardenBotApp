/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.apollographql.apollo.api.Response
import com.example.gardenbotapp.NewDeviceSubscription
import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {

    suspend fun newDeviceSub(deviceName: String): Flow<Response<NewDeviceSubscription.Data>>

    suspend fun activateDevice(deviceName: String, userId: String, token: String): String

}