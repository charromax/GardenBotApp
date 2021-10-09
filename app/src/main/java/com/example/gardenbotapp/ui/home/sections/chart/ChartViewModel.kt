/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import androidx.lifecycle.*
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.data.domain.ChartRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.remote.model.Measure
import com.example.gardenbotapp.di.ApplicationDefaultScope
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import com.example.gardenbotapp.util.Errors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import javax.inject.Inject

const val EXP_TOKEN = "Invalid/Expired token"
const val MAX_ALLOWED_TEMPERATURE = 40
const val MIN_ALLOWED_TEMPERATURE = 19
const val MAX_ALLOWED_AIR_HUMIDITY = 85
const val MIN_ALLOWED_AIR_HUMIDITY = 45
const val MAX_ALLOWED_SOIL_HUMIDITY = 90
const val MIN_ALLOWED_SOIL_HUMIDITY = 15

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
        subscribeToMeasures()
    }

    override fun onCleared() {
        super.onCleared()
        defScope.cancel()
    }

    /**
     * manually update current measure to trigger live views
     */
    fun manualUpdateNewMeasure(newMeasure: Measure) {
        _measureSub.value = newMeasure
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
                            _measureSub.value = Measure.fromResponse(res)
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
            _measures.value?.let { listSoFar.addAll(it) }
            listSoFar.add(measure)
            _measures.postValue(listSoFar)
        }

    }

    fun populateChartData() {
        viewModelScope.launch {
            try {
                val currentToken = preferencesManager.tokenFlow.first()
                val currentDevice = preferencesManager.deviceIdFlow.first()
                chartRepository.getMeasuresForDevice(currentDevice, currentToken)
                    .collect {
                        _measures.value = it
                    }
            } catch (e: Exception) {
                e.message?.let { message ->
                    chartEventsChannel.send(Errors.HomeError(message))
                }
            }
        }
    }

    val liveAirHumData: LiveData<Float> =
        Transformations.switchMap(_measureSub) {
            liveData { emit(it.airHum.toFloat()) }
        }


    val liveAirTempData: LiveData<Float> =
        Transformations.switchMap(_measureSub) {
            liveData(defScope.coroutineContext) {
                val temperaturePercent = (it.airTemp * 100) / MAX_ALLOWED_TEMPERATURE
                emit(temperaturePercent.toFloat())
            }
        }

    val liveSoilHumData: LiveData<Float> =
        Transformations.switchMap(_measureSub) {
            liveData { emit(it.soilHum.toFloat()) }
        }
}
