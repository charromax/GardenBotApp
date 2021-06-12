/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.remote

import android.util.Log
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.RefreshTokenQuery

class GardenBotRepository : GardenBotContract {
    companion object {
        const val TAG = "GARDENREPO"
    }

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