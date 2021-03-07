/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home

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
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gardenBotRepository: GardenBotRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {


    private val _measures = MutableLiveData<List<Measure>>()
    val measures: LiveData<List<Measure>> get() = _measures
    private val homeEventsChannel = Channel<HomeEvents>()
    val homeEvents = homeEventsChannel.receiveAsFlow()
    private val _measureSub = MutableLiveData<Measure>()
    val measureSub: LiveData<Measure> get() = _measureSub

    init {
        populateChartData()
        subscribeToMeasures()
    }

    private fun subscribeToMeasures() {
        viewModelScope.launch {
            try {
                gardenBotRepository.newMeasureSub()
                    .retryWhen { _, attempt ->
                        delay((attempt * 1000))    //exp delay
                        true
                    }
                    .collect { res ->
                        if (res.hasErrors() || res.data?.newMeasure == null) {
                            homeEventsChannel.send(HomeEvents.SubError("ERROR: ${res.errors?.map { it.message }}"))
                        } else {
                            _measureSub.value = Measure(
                                id = res.data?.newMeasure?.id ?: "",
                                airTemp = res.data?.newMeasure?.airTemp ?: 0.0,
                                airHum = res.data?.newMeasure?.airHum ?: 0.0,
                                soilHum = res.data?.newMeasure?.soilHum ?: 0.0,
                                createdAt = res.data?.newMeasure?.createdAt ?: ""
                            )
                        }
                    }
            } catch (e: ApolloException) {
                homeEventsChannel.send(HomeEvents.SubError("ERROR: ${e.message}"))
            }
        }
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
                            Log.i(TAG, "populateChartData: refresh token...")
                            val res = async { gardenBotRepository.refreshToken(currentToken) }
                            val newToken = res.await()
                            preferencesManager.updateToken(newToken?.refreshToken)
                            populateChartData(newToken?.refreshToken)
                        } catch (e: ApolloException) {
                            homeEventsChannel.send(HomeEvents.TokenError(e.message))
                            preferencesManager.updateToken("")
                        }
                    } else {
                        homeEventsChannel.send(HomeEvents.HomeError(message))
                    }
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareDataSetForChart(list: List<Measure>): ArrayList<ILineDataSet>? {
        val sortedList = list.sortedBy {
            it.createdAt
        }
        Log.i(TAG, "prepareDataSetForChart: $sortedList")

        sortedList.let { list ->
            val airTSet = list.mapIndexed { index, measure ->
                Entry(
                    index.toFloat(),
                    measure.airTemp.toFloat()
                )
            }
            val airHSet = list.mapIndexed { index, measure ->
                Entry(
                    index.toFloat(),
                    measure.airHum.toFloat()
                )
            }
            val soilHSet = list.mapIndexed { index, measure ->
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
        return null
    }

    companion object {
        const val TAG = "HOMEVIEWMODEL"
        const val EXP_TOKEN = "Invalid/Expired token"
    }

    sealed class HomeEvents {
        data class TokenError(val message: String?) : HomeEvents()
        data class HomeError(val message: String) : HomeEvents()
        data class SubError(val message: String) : HomeEvents()
    }
}