/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

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
import com.example.gardenbotapp.util.toDate
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

@HiltViewModel
class ChartCalculationsViewModel @Inject constructor(
) : GardenBotBaseViewModel() {

    @Inject
    @ApplicationDefaultScope
    lateinit var defScope: CoroutineScope

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
                        .take(15)
                        .asSequence()
                        .filter { sensorData -> sensorData.airTemp > 0 }
                        .sortedBy { it.createdAt.toDate() }
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
                    LineDataSet(entries, "Temperatura Ambiental").apply {
                        valueFormatter = DateLabelFormatter()
                        color = ContextCompat.getColor(context, R.color.blue)
                        setDrawCircles(false)
                        lineWidth = 4f
                        valueTextColor =
                            ContextCompat.getColor(context, R.color.gardenbot_green_dark)
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