/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.remote.model.Measure
import com.example.gardenbotapp.di.ApplicationDefaultScope
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

const val LINE_WIDTH = 3f
const val VALUE_TEXT_SIZE = 10f

//defines the number of measures shown in home screen charts
const val MAX_ENTRIES = 10

@HiltViewModel
class ChartCalculationsViewModel @Inject constructor(
) : GardenBotBaseViewModel() {

    @Inject
    @ApplicationDefaultScope
    lateinit var defScope: CoroutineScope

    @SuppressLint("StaticFieldLeak")
    @Inject
    @ApplicationContext
    lateinit var context: Context
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
                        .filter { sensorData -> sensorData.airTemp > 0 }
                        .takeLast(MAX_ENTRIES)
                        .asSequence()
                        .mapIndexed { index, entry ->
                            Entry(index.toFloat(), entry.airTemp.toFloat(), entry.createdAt)
                        }
                        .toMutableList()
                )
            }
        }

    val airTempDataSet: LiveData<LineDataSet> =
        Transformations.switchMap(airTempEntrySet) { entries ->
            liveData(context = defScope.coroutineContext) {
                emit(
                    LineDataSet(entries, context.getString(R.string.air_temp_chart_label)).apply {
                        valueFormatter = ShortDateLabelFormatter()
                        color = ContextCompat.getColor(context, R.color.blue)
                        setDrawCircles(false)
                        lineWidth = LINE_WIDTH
                        valueTextSize = VALUE_TEXT_SIZE
                        valueTextColor =
                            ContextCompat.getColor(context, R.color.gardenbot_green_dark)
                        mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                    }
                )
            }
        }

    val airTempLineData: LiveData<LineData> = Transformations.switchMap(airTempDataSet) {
        liveData(context = defScope.coroutineContext) {
            emit(LineData(it))
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    val airHumEntrySet: LiveData<MutableList<Entry>> =
        Transformations.switchMap(_measures) { rawList ->
            liveData(context = defScope.coroutineContext) {
                emit(
                    rawList
                        .filter { sensorData -> sensorData.airHum > 0 }
                        .takeLast(MAX_ENTRIES)
                        .asSequence()
                        .mapIndexed { index, entry ->
                            Entry(index.toFloat(), entry.airHum.toFloat(), entry.createdAt)
                        }
                        .toMutableList()
                )
            }
        }

    val airHumDataSet: LiveData<LineDataSet> =
        Transformations.switchMap(airHumEntrySet) { entries ->
            liveData(context = defScope.coroutineContext) {
                emit(
                    LineDataSet(entries, context.getString(R.string.air_hum_chart_label)).apply {
                        valueFormatter = ShortDateLabelFormatter()
                        color = ContextCompat.getColor(context, R.color.gardenbot_green)
                        setDrawCircles(false)
                        lineWidth = LINE_WIDTH
                        valueTextSize = VALUE_TEXT_SIZE
                        valueTextColor =
                            ContextCompat.getColor(context, R.color.gardenbot_green_dark)
                        mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                    }
                )
            }
        }

    val airHumLineData: LiveData<LineData> = Transformations.switchMap(airHumDataSet) {
        liveData(context = defScope.coroutineContext) {
            emit(LineData(it))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val soilHumEntrySet: LiveData<MutableList<Entry>> =
        Transformations.switchMap(_measures) { rawList ->
            liveData(context = defScope.coroutineContext) {
                emit(
                    rawList
                        .filter { sensorData -> sensorData.soilHum > 0 }
                        .takeLast(MAX_ENTRIES)
                        .asSequence()
                        .mapIndexed { index, entry ->
                            Entry(index.toFloat(), entry.soilHum.toFloat(), entry.createdAt)
                        }
                        .toMutableList()
                )
            }
        }

    val soilHumDataSet: LiveData<LineDataSet> =
        Transformations.switchMap(soilHumEntrySet) { entries ->
            liveData(context = defScope.coroutineContext) {
                emit(
                    LineDataSet(entries, context.getString(R.string.soil_hum_chart_label)).apply {
                        valueFormatter = ShortDateLabelFormatter()
                        color = ContextCompat.getColor(context, R.color.red)
                        setDrawCircles(false)
                        lineWidth = LINE_WIDTH
                        valueTextSize = VALUE_TEXT_SIZE
                        valueTextColor =
                            ContextCompat.getColor(context, R.color.gardenbot_green_dark)
                        mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                    }
                )
            }
        }

    val soilHumLineData: LiveData<LineData> = Transformations.switchMap(soilHumDataSet) {
        liveData(context = defScope.coroutineContext) {
            emit(LineData(it))
        }
    }
}