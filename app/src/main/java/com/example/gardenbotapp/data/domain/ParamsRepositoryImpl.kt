package com.example.gardenbotapp.data.domain

import android.util.Log
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.*
import com.example.gardenbotapp.data.remote.Client
import com.example.gardenbotapp.type.ParamsPayload
import com.example.gardenbotapp.type.StatusRequest
import kotlinx.coroutines.flow.Flow

class ParamsRepositoryImpl: ParametersRepository, GardenBotRepositoryImpl()  {
    companion object {
        const val TAG = "PARAMS_REPO"
    }
    override suspend fun sendParamsStatusRequest(
        statusRequest: StatusRequest,
        token: String
    ): String? {
        try {
            val response =
                Client.getInstance(token).mutate(StatusRequestMutation(statusRequest)).await()
            if (response.data == null || response.hasErrors()) {
                Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.statusRequest
                }
            }
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw e
        }
        return null
    }

    override suspend fun changeParams(payload: ParamsPayload, token: String): String? {
        try {
            val response =
                Client.getInstance(token).mutate(SendAutoPilotParamsMutation(payload))
                    .await()

            if (response.data == null || response.hasErrors()) {
                Log.i(TAG, "ERROR: ${response.errors?.map { it.message }}")
                throw ApolloException("${response.errors?.map { it.message }}")
            } else {
                response.data?.let {
                    return it.sendAutoPilotParams
                }
            }
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw e
        }
        return null
    }

    override suspend fun paramsStatusResponseSub(deviceId: String): Flow<Response<NewAutoPilotParamsResponseSubscription.Data>> {
        try {
            return Client.getInstance().subscribe(NewAutoPilotParamsResponseSubscription(deviceId)).toFlow()
        } catch (e: ApolloException) {
            Log.i(TAG, "ERROR: ${e.message}")
            throw ApolloException("${e.message}")
        }
    }

}