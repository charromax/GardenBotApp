/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data

import com.apollographql.apollo.ApolloClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient

object Client {
    const val TAG = "CLIENT"
    private const val BASE_URL = "http://192.168.0.6:5000/graphql"
    private var apolloClient: ApolloClient? = null

    fun getInstance(token: String? = null): ApolloClient {
        return apolloClient ?: ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(token))
                    .build()
            ).build()
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