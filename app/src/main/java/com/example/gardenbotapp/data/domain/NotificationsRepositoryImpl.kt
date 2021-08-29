/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import android.util.Log
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.NewNotificationSubscription
import com.example.gardenbotapp.data.remote.Client
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class NotificationsRepositoryImpl : GardenBotRepositoryImpl(), NotificationsRepository {

    companion object {
        const val TAG = "NOTIFICATIONS_REPO"
    }

    override suspend fun newNotificationSub(deviceId: String): Flow<Response<NewNotificationSubscription.Data>> {
        try {
            return Client.getInstance().subscribe(NewNotificationSubscription(deviceId)).toFlow()
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw ApolloException("${e.message}")
        }
    }
}