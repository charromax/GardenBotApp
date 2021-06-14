/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import android.util.Log
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.ActivateDeviceMutation
import com.example.gardenbotapp.NewDeviceSubscription
import com.example.gardenbotapp.data.remote.Client
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class OnboardingRepositoryImpl : GardenBotRepositoryImpl(), OnboardingRepository {

    companion object {
        const val TAG = "ONBOARDING_REPO"
    }

    override suspend fun newDeviceSub(deviceName: String): Flow<Response<NewDeviceSubscription.Data>> {
        try {
            return Client.getInstance().subscribe(NewDeviceSubscription(deviceName)).toFlow()
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw ApolloException("${e.message}")
        }
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
}