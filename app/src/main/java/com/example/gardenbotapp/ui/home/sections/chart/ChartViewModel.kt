/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.data.domain.ChartRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.remote.model.Measure
import com.example.gardenbotapp.di.ApplicationDefaultScope
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import com.example.gardenbotapp.ui.home.HomeViewModel
import com.example.gardenbotapp.util.Errors
import com.example.gardenbotapp.util.toDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import javax.inject.Inject

const val EXP_TOKEN = "Invalid/Expired token"

@HiltViewModel
class ChartViewModel @Inject constructor(
    @ApplicationDefaultScope private val defScope: CoroutineScope,
    private val chartRepository: ChartRepository,
    private val preferencesManager: PreferencesManager
) : GardenBotBaseViewModel() {

    private val _measures = MutableLiveData<List<Measure>>()
    val measures: LiveData<List<Measure>> get() = _measures
    private val chartEventsChannel = Channel<Errors>()
    val chartEvents = chartEventsChannel.receiveAsFlow()
    private val _measureSub = MutableLiveData<Measure>()
    val measureSub: LiveData<Measure> get() = _measureSub

    init {
        populateChartData()
        subscribeToMeasures()
    }

    override fun onCleared() {
        super.onCleared()
        defScope.cancel()
    }

    private fun subscribeToMeasures() {
        viewModelScope.launch {
            try {
                val deviceId = preferencesManager.deviceIdFlow.first()
                chartRepository.newMeasureSub(deviceId)
                    .retryWhen { _, attempt ->
                        delay((attempt * 1000))    //exp delay
                        true
                    }
                    .collect { res ->
                        if (res.hasErrors() || res.data?.newMeasure == null) {
                            chartEventsChannel.send(Errors.SubError("ERROR: ${res.errors?.map { it.message }}"))
                        } else {
                            val measure = Measure(
                                id = res.data?.newMeasure?.id ?: "",
                                airTemp = res.data?.newMeasure?.airTemp ?: 0.0,
                                airHum = res.data?.newMeasure?.airHum ?: 0.0,
                                soilHum = res.data?.newMeasure?.soilHum ?: 0.0,
                                createdAt = res.data?.newMeasure?.createdAt ?: ""
                            )
                            _measureSub.value = measure
                        }
                    }
            } catch (e: ApolloException) {
                chartEventsChannel.send(Errors.SubError("ERROR: ${e.message}"))
            }
        }
    }



    fun refreshChartData(measure: Measure) {
        defScope.launch {
            val listSoFar = arrayListOf<Measure>()
            _measures.value?.forEach { listSoFar.add(it) }
            Log.i("charromax", "refreshChartData: size: ${listSoFar.size}")
            listSoFar.add(measure)
            Log.i("charromax", "refreshChartData: size: ${listSoFar.size}")
            Log.i("charromax", "refreshChartData: before sort ${listSoFar.map { it.createdAt }}")
            val sortedList = listSoFar.sortedWith(compareBy { it.createdAt.toDate() }).reversed()
            Log.i("charromax", "refreshChartData: after sort ${sortedList.map { it.createdAt }}")
            _measures.postValue(sortedList)
        }

    }

    fun populateChartData(token: String? = null) {
        val currentToken = token ?: runBlocking { preferencesManager.tokenFlow.first() }
        viewModelScope.launch {
            try {
                val currentDevice = preferencesManager.deviceIdFlow.first()
                chartRepository.getMeasuresForDevice(currentDevice, currentToken)
                    .collect {
                        _measures.value = it
                    }
            } catch (e: ApolloException) {
                e.message?.let { message ->
                    if (message.contains(EXP_TOKEN)) {
                        try {
                            Log.i(HomeViewModel.TAG, "populateChartData: refresh token...")
                            val res = async { refreshToken(currentToken) }
                            val newToken = res.await()
                            preferencesManager.updateToken(newToken?.refreshToken)
                            populateChartData(newToken?.refreshToken)
                        } catch (e: ApolloException) {
                            chartEventsChannel.send(Errors.TokenError(e.message))
                            preferencesManager.updateToken("")
                        }
                    } else {
                        chartEventsChannel.send(Errors.HomeError(message))
                    }
                }
            }
        }
    }
}
