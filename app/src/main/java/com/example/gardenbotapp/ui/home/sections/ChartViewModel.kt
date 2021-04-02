/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.GardenBotRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.model.Measure
import com.example.gardenbotapp.ui.home.HomeViewModel
import com.example.gardenbotapp.util.Errors
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val EXP_TOKEN = "Invalid/Expired token"

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val gardenBotRepository: GardenBotRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : ViewModel() {

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
                gardenBotRepository.newMeasureSub(deviceId)
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
                gardenBotRepository.getMeasuresForDevice(currentDevice, currentToken)
                    .collect {
                        _measures.value = it
                    }
            } catch (e: ApolloException) {
                e.message?.let { message ->
                    if (message.contains(EXP_TOKEN)) {
                        try {
                            Log.i(HomeViewModel.TAG, "populateChartData: refresh token...")
                            val res = async { gardenBotRepository.refreshToken(currentToken) }
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareDataSetForChart(context: Context, list: List<Measure>): ArrayList<ILineDataSet> {
        list.sortedBy {
            it.createdAt
        }.let { theList ->
            val airTSet = theList.mapIndexed { index, measure ->
                Entry(
                    index.toFloat(),
                    measure.airTemp.toFloat()
                )
            }
            val airHSet = theList.mapIndexed { index, measure ->
                Entry(
                    index.toFloat(),
                    measure.airHum.toFloat()
                )
            }
            val soilHSet = theList.mapIndexed { index, measure ->
                Entry(
                    index.toFloat(),
                    measure.soilHum.toFloat()
                )
            }

            val lines = ArrayList<ILineDataSet>()

            lines.add(LineDataSet(airTSet, "Air Temp").also {
                it.axisDependency = YAxis.AxisDependency.LEFT
                it.color = context.getColor(R.color.red)
            })
            lines.add(LineDataSet(airHSet, "Air Hum").also {
                it.axisDependency = YAxis.AxisDependency.LEFT
                it.color = context.getColor(R.color.green)
            })
            lines.add(LineDataSet(soilHSet, "SoilHum").also {
                it.axisDependency = YAxis.AxisDependency.LEFT
                it.color = context.getColor(R.color.blue)
            })
            return lines
        }
    }
}
