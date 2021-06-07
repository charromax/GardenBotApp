/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.example.gardenbotapp.data.remote.Client.AUTH
import okhttp3.Interceptor
import okhttp3.OkHttpClient

object Client {
    const val TAG = "CLIENT"
    private const val BASE_URL = "https://charr0bot.herokuapp.com/graphql"
    private const val WSS_URL = "wss://charr0bot.herokuapp.com/graphql"
    const val AUTH = "Authorization"
    private var apolloClient: ApolloClient? = null

    fun getInstance(token: String? = null): ApolloClient {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor(token)).build()
        return apolloClient ?: ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .subscriptionTransportFactory(
                WebSocketSubscriptionTransport.Factory(
                    WSS_URL,
                    okHttpClient
                )
            )
            .build()
    }
}

class AuthInterceptor(val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader(AUTH, if (token != null) "Bearer $token" else "")
            .build()
        return chain.proceed(request)

    }

}