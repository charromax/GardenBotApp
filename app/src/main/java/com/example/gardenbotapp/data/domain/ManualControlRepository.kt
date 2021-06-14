/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.example.gardenbotapp.type.Payload

interface ManualControlRepository : GardenBotRepository {
    suspend fun sendMqttOrder(payload: Payload, token: String): String?
}