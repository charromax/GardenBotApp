/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.data.remote.GardenBotContract
import com.example.gardenbotapp.type.RegisterInput

interface RegisterUserRepository : GardenBotContract {
    suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register?
}