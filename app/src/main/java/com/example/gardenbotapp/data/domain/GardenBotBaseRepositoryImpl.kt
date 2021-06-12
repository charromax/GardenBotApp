/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.example.gardenbotapp.RefreshTokenQuery
import com.example.gardenbotapp.data.remote.GardenBotContract

open class GardenBotBaseRepositoryImpl : GardenBotContract {

    override suspend fun refreshToken(token: String): RefreshTokenQuery.Data? {
        TODO("Not yet implemented")
    }
}