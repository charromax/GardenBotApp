/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.remote

import com.example.gardenbotapp.RefreshTokenQuery

interface GardenBotContract {

    suspend fun refreshToken(token: String): RefreshTokenQuery.Data?
}