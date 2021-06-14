/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.type.RegisterInput

interface RegisterUserRepository : GardenBotRepository {
    suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register?
}