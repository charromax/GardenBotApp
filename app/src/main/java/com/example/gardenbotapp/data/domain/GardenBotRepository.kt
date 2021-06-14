/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.example.gardenbotapp.RefreshTokenQuery

interface GardenBotRepository {
    suspend fun refreshToken(token: String): RefreshTokenQuery.Data?
}