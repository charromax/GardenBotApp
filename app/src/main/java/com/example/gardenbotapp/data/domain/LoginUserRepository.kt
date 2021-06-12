/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.example.gardenbotapp.LoginUserMutation
import com.example.gardenbotapp.data.remote.GardenBotContract

interface LoginUserRepository : GardenBotContract {

    suspend fun loginUser(username: String, password: String): LoginUserMutation.Login?
}