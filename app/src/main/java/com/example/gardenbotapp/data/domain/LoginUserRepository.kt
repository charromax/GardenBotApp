/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.example.gardenbotapp.LoginUserMutation

interface LoginUserRepository : GardenBotRepository {

    suspend fun loginUser(username: String, password: String): LoginUserMutation.Login?
}