/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp

import com.example.gardenbotapp.data.domain.GardenBotRepository

open class FakeGardenbotRepository : GardenBotRepository {

    override suspend fun refreshToken(token: String): RefreshTokenQuery.Data? {
        return RefreshTokenQuery.Data("ABrandNewFreshToken")
    }

}