/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.util.Log
import androidx.lifecycle.*
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.NewMeasureSubscription
import com.example.gardenbotapp.RefreshTokenQuery
import com.example.gardenbotapp.data.domain.ChartRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.remote.model.Measure
import com.example.gardenbotapp.ui.home.HomeViewModel
import com.example.gardenbotapp.util.Errors
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

const val EXP_TOKEN = "Invalid/Expired token"

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val chartRepository: ChartRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel(), ChartRepository {

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

    private fun subscribeToMeasures() {
        viewModelScope.launch {
            try {
                val deviceId = preferencesManager.deviceIdFlow.first()
                newMeasureSub(deviceId)
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

    val airTempDataset: LiveData<MutableList<Float>> =
        Transformations.switchMap(_measures) { rawList ->
            liveData {
                emit(
                    rawList
                        .asSequence()
                        .filter { sensorData -> sensorData.airTemp > 0 }
                        .map { measure -> measure.airTemp.toFloat() }
                        .toMutableList()
                )
            }
        }

    val airHumDataset: LiveData<MutableList<Float>> =
        Transformations.switchMap(_measures) { rawList ->
            liveData {
                emit(
                    rawList
                        .asSequence()
                        .filter { sensorData -> sensorData.airHum > 0 }
                        .map { measure -> measure.airHum.toFloat() }
                        .toMutableList()
                )
            }
        }

    val soilHumDataset: LiveData<MutableList<Float>> =
        Transformations.switchMap(_measures) { rawList ->
            liveData {
                emit(
                    rawList
                        .asSequence()
                        .filter { sensorData -> sensorData.soilHum > 0 }
                        .map { measure -> measure.soilHum.toFloat() }
                        .toMutableList()
                )
            }
        }

    fun refreshChartData(measure: Measure) {
        val listSoFar = arrayListOf<Measure>()
        _measures.value?.let { listSoFar.addAll(it) }
        listSoFar.add(measure)
        _measures.value = listSoFar
    }

    private fun populateChartData(token: String? = null) {
        val currentToken = token ?: runBlocking { preferencesManager.tokenFlow.first() }
        viewModelScope.launch {
            try {
                val currentDevice = preferencesManager.deviceIdFlow.first()
                getMeasuresForDevice(currentDevice, currentToken)
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

    val airHumChartModel: LiveData<AAChartModel> =
        Transformations.switchMap(airHumDataset) { dataset ->
            liveData {
                val elements = AASeriesElement().apply {
                    name("Humedad ambiental")
                    data(dataset.toTypedArray())
                }
                val aaChartModel = AAChartModel().apply {
                    chartType(AAChartType.Bar)
                    title("title")
                    subtitle("subtitle")
                    backgroundColor("#4b2b7f")
                    dataLabelsEnabled(true)
                    series(
                        arrayOf(elements)
                    )
                }
                emit(aaChartModel)
            }
        }

    override suspend fun getMeasuresForDevice(
        deviceId: String,
        token: String
    ): Flow<List<Measure>> {
        return chartRepository.getMeasuresForDevice(deviceId, token)
    }

    override suspend fun newMeasureSub(deviceId: String): Flow<Response<NewMeasureSubscription.Data>> {
        return chartRepository.newMeasureSub(deviceId)
    }

    override suspend fun refreshToken(token: String): RefreshTokenQuery.Data? {
        return chartRepository.refreshToken(token)
    }

}
