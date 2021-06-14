/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.domain

import android.util.Log
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.RefreshTokenQuery
import com.example.gardenbotapp.data.remote.Client
import javax.inject.Singleton

const val TAG = "BASE_REPO"

@Singleton
open class GardenBotRepositoryImpl : GardenBotRepository {

    override suspend fun refreshToken(token: String): RefreshTokenQuery.Data? {
        try {
            val response = Client.getInstance(token).query(RefreshTokenQuery()).await()
            if (response.data == null || response.hasErrors()) {
                Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it
                }
            }
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw e
        }
        return null
    }
}