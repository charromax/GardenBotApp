package com.example.gardenbotapp.data.remote

import android.content.Context
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.LoginUserMutation
import com.example.gardenbotapp.MeasuresQuery
import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.data.model.User
import com.example.gardenbotapp.type.RegisterInput
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.lang.Exception

object Client {
    private const val TAG = "CLIENT"
    private const val BASE_URL = "http://192.168.0.6:5000/graphql"
    private val apolloClient: ApolloClient by lazy {
        ApolloClient.builder()
            .serverUrl(BASE_URL)
//            .okHttpClient(
//                OkHttpClient.Builder()
//        .addInterceptor(AuthInterceptor())
//        .build()
//    )
            .build()
    }

    init {
        Log.i(TAG, "Client started...")
    }

    @ExperimentalCoroutinesApi
    suspend fun getAllMeasures(deviceId: String): Flow<List<MeasuresQuery.GetMeasure?>> {
        if (deviceId.isNotBlank()) {
            try {
                val response = apolloClient.query(MeasuresQuery(deviceId))
                    .await()
                if (response.data?.getMeasures == null || response.hasErrors()) {
                    Log.i(TAG, "getAllMeasures: ERROR: ${response.errors?.map { it.message }}")
                    return emptyFlow()
                } else {
                    response.data?.let {
                        return flowOf(it.getMeasures)
                    }
                }
            } catch (e: ApolloException) {
                Log.i(TAG, "getAllMeasures: ERROR: ${e.message}")
            }
        }
        return emptyFlow()
    }

    suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register? {
        try {
            val response = apolloClient
                .mutate(RegisterUserMutation(userInput))
                .await()

            if (response.data?.register == null || response.hasErrors()) {
                Log.i(TAG, "registerNewUser: ERROR: ${response.errors?.map { it.message }}")
                return null
            } else {
                response.data?.let {
                    return it.register
                }
            }

        } catch (e: ApolloException) {
            Log.i(TAG, "registerNewUser: ERROR: ${e.message}")
        }
        return null
    }

    suspend fun loginUser(username: String, password: String): LoginUserMutation.Login? {
        try {
            val response = apolloClient
                .mutate(LoginUserMutation(username, password))
                .await()

            if (response.data?.login == null || response.hasErrors()) {
                Log.i(TAG, "LoginUser: ERROR: ${response.errors?.map { it.message }}")
                throw Exception(response.errors?.joinToString { it.message })
            } else {
                response.data?.let {
                    return it.login
                }
            }
        } catch (e: ApolloException) {
            Log.i(TAG, "loginUser: ERROR: ${e.message}")
        }
        return null

    }
}

class AuthInterceptor() : Interceptor {
    private val token: Nothing =
        TODO("after user login or registration save token in preferences manager to be used for requests")

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", token)
            .build()

        return chain.proceed(request)
    }

}


