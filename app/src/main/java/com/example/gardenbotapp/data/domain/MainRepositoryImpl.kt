package com.example.gardenbotapp.data.domain

import android.util.Log
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.CheckTokenQuery
import com.example.gardenbotapp.data.remote.Client

class MainRepositoryImpl: MainRepository, GardenBotRepositoryImpl() {
    companion object {
        const val TAG = "MAIN_REPO"
    }
    override suspend fun checkToken(token:String): Boolean {
        try {
            val response = Client.getInstance(token).query(CheckTokenQuery()).await()
            if (response.data?.checkToken == null || response.hasErrors()) {
                Log.i(ChartRepositoryImpl.TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.checkToken
                }
            }
        } catch (e: ApolloException) {
            Log.i(TAG, "checkToken: ${e.message}")
            return false
        }
        return false
    }
}