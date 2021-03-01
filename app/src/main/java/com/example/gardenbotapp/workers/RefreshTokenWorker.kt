/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gardenbotapp.data.GardenBotRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first

@HiltWorker
class RefreshTokenWorker @AssistedInject constructor(
    private val preferencesManager: PreferencesManager,
    @ApplicationContext private val context: Context,
    private val gardenBotRepository: GardenBotRepository,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        //TODO: retrieve token from preferences manager
        // then send to server for refreshing every 6h
        val token = preferencesManager.tokenFlow.first()
        val newToken = gardenBotRepository.refreshToken(token)
        newToken?.let {
            Log.i(TAG, "doWork: token: ${newToken.refreshToken}")
            preferencesManager.updateToken(it.refreshToken)
        }
        return Result.success()
    }


    companion object {
        const val TAG = "WORKER"
    }

}