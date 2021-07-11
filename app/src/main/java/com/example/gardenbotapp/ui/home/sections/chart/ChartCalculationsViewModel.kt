/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.remote.model.Measure
import com.example.gardenbotapp.di.ApplicationDefaultScope
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import com.example.gardenbotapp.util.toLocalDate
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

@HiltViewModel
class ChartCalculationsViewModel @Inject constructor(
) : GardenBotBaseViewModel() {

    @Inject
    @ApplicationDefaultScope
    lateinit var defScope: CoroutineScope
    private val _measures = MutableLiveData<List<Measure>>()

    override fun onCleared() {
        super.onCleared()
        defScope.cancel()
    }

    fun initModelCalculations(measures: List<Measure>) {
        _measures.value = measures
    }

    /**
     * returns list of filtered AIRTEMP measures with creation date
     * as extra data
     */
    @RequiresApi(Build.VERSION_CODES.O)
    val airTempEntrySet: LiveData<MutableList<Entry>> =
        Transformations.switchMap(_measures) { rawList ->
            liveData(context = defScope.coroutineContext) {
                emit(
                    rawList
                        .asSequence()
                        .filter { sensorData -> sensorData.airTemp > 0 }
                        .groupBy {
                            Log.i("charr0max", "date: ${it.createdAt.toLocalDate()?.dayOfYear}")
                            it.createdAt.toLocalDate()?.dayOfYear
                        }
                        .map { entry ->
                            entry.value.map { it.airTemp }.average()
                        }
                        .mapIndexed { index, airTemp ->
                            Entry(index.toFloat(), airTemp.toFloat())
                        }
                        .toMutableList()
                )
            }
        }

    val airTempDataSet: LiveData<LineDataSet> =
        Transformations.switchMap(airTempEntrySet) { entries ->
            liveData(context = defScope.coroutineContext) {
                emit(
                    LineDataSet(entries, "Temperatura Ambiental").apply {
                        valueFormatter = TemperatureLabelFormatter()
                        color = R.color.gardenbot_green
                        setDrawCircles(false)
                        lineWidth = 4f
                        valueTextColor = R.color.gardenbot_green_dark
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                    }
                )
            }
        }

    val airTempLineData: LiveData<LineData> = Transformations.switchMap(airTempDataSet) {
        liveData(context = defScope.coroutineContext) {
            emit(LineData(it))
        }
    }


    val airHumDataset: LiveData<MutableList<Float>> =
        Transformations.switchMap(_measures) { rawList ->
            liveData(context = defScope.coroutineContext) {
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
            liveData(context = defScope.coroutineContext) {
                emit(
                    rawList
                        .asSequence()
                        .filter { sensorData -> sensorData.soilHum > 0 }
                        .map { measure -> measure.soilHum.toFloat() }
                        .toMutableList()
                )
            }
        }
}