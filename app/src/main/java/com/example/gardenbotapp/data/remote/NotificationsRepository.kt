/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.remote

import com.apollographql.apollo.api.Response
import com.example.gardenbotapp.NewNotificationSubscription
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository : GardenBotContract {
    suspend fun newNotificationSub(deviceId: String): Flow<Response<NewNotificationSubscription.Data>>
}