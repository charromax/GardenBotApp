/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import android.util.Log
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.data.remote.Client
import com.example.gardenbotapp.data.remote.GardenBotRepository
import com.example.gardenbotapp.type.RegisterInput

class RegisterUserRepositoryImpl : GardenBotBaseRepositoryImpl(), RegisterUserRepository {

    override suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register? {
        try {
            val response = Client.getInstance().mutate(RegisterUserMutation(userInput)).await()
            if (response.data?.register == null || response.hasErrors()) {
                Log.i(GardenBotRepository.TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.register
                }
            }
        } catch (e: ApolloException) {
            Log.i(GardenBotRepository.TAG, "ERROR: ${e.message}")
            throw ApolloException("${e.message}")
        }
        return null
    }
}