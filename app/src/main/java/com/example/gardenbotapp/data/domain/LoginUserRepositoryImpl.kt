/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import android.util.Log
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.LoginUserMutation
import com.example.gardenbotapp.data.remote.Client
import com.example.gardenbotapp.data.remote.GardenBotRepository

class LoginUserRepositoryImpl : LoginUserRepository {

    override suspend fun loginUser(username: String, password: String): LoginUserMutation.Login? {
        try {
            val response =
                Client.getInstance().mutate(LoginUserMutation(username, password)).await()

            if (response.data?.login == null || response.hasErrors()) {
                Log.i(GardenBotRepository.TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.login
                }
            }
        } catch (e: ApolloException) {
            Log.i(GardenBotRepository.TAG, "ERROR: ${e.printStackTrace()}")
            throw ApolloException("${e.message}")
        }
        return null
    }
}