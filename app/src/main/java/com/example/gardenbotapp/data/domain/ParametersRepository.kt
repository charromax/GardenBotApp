/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.example.gardenbotapp.type.Payload

interface ParametersRepository : GardenBotRepository {
    suspend fun getCurrentParams(deviceId: String, token: String)
    suspend fun changeParams(deviceId: String, token: String, params: Payload): String?

}