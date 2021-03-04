/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.remote

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.*
import com.example.gardenbotapp.type.RegisterInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import okhttp3.Interceptor
import okhttp3.OkHttpClient

private const val TAG = "CLIENT"
private const val BASE_URL = "http://192.168.0.6:5000/graphql"

class Client(val token: String? = null) {

    private val apolloClient: ApolloClient by lazy {
        ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(token))
                    .build()
            )
            .build()
    }

    init {
        Log.i(TAG, "Client started...")
    }

    suspend fun getAllMeasures(deviceId: String): Flow<List<MeasuresQuery.GetMeasure?>> {
        if (deviceId.isNotBlank()) {
            try {
                val response = apolloClient.query(MeasuresQuery(deviceId))
                    .await()
                if (response.data?.getMeasures == null || response.hasErrors()) {
                    Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                    throw ApolloException("${response.errors?.map { it.message }}")
                } else {
                    response.data?.let {
                        return flowOf(it.getMeasures)
                    }
                }
            } catch (e: ApolloException) {
                Log.i(TAG, "ERROR: ${e.message}")
                throw ApolloException("${e.message}")
            }
        }
        return emptyFlow()
    }

    suspend fun activateDevice(deviceName: String, userId: String): String {
        if (deviceName.isNotEmpty()) {
            try {
                val response =
                    apolloClient.mutate(ActivateDeviceMutation(deviceName, userId)).await()
                if (response.data?.activateDevice == null || response.hasErrors()) {
                    Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                    throw ApolloException("${response.errors?.map { it.message }}")
                } else {
                    response.data?.let {
                        return it.activateDevice.id
                    }
                }
            } catch (e: ApolloException) {
                Log.i(TAG, "ERROR: ${e.message}")
                throw ApolloException("${e.message}")
            }
        }
        return ""
    }

    suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register? {
        try {
            val response = apolloClient
                .mutate(RegisterUserMutation(userInput))
                .await()

            if (response.data?.register == null || response.hasErrors()) {
                Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.register
                }
            }

        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw ApolloException("${e.message}")
        }
        return null
    }

    suspend fun loginUser(username: String, password: String): LoginUserMutation.Login? {
        try {
            val response = apolloClient
                .mutate(LoginUserMutation(username, password))
                .await()

            if (response.data?.login == null || response.hasErrors()) {
                Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.login
                }
            }
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw ApolloException("${e.message}")
        }
        return null

    }

    suspend fun refreshToken(): RefreshTokenQuery.Data? {
        try {
            val response = apolloClient.query(RefreshTokenQuery()).await()

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

class AuthInterceptor(val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", if (token != null) "Bearer $token" else "")
            .build()
        return chain.proceed(request)

    }

}


