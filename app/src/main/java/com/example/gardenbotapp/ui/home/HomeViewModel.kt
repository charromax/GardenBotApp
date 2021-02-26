/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.gardenbotapp.MeasuresQuery
import com.example.gardenbotapp.data.GardenBotRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
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


    private val _measures = MutableLiveData<List<MeasuresQuery.GetMeasure?>>()
    val measures: LiveData<List<MeasuresQuery.GetMeasure?>> get() = _measures

    init {
        val token = runBlocking { preferencesManager.tokenFlow.first() }
        viewModelScope.launch {
            gardenBotRepository.getMeasuresForDevice("601599a09606f008af118b79", token).collect {
                _measures.value = it
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareDataSetForChart(list: List<MeasuresQuery.GetMeasure?>?): ArrayList<ILineDataSet>? {
        val sortedList = list?.sortedBy {
            it?.createdAt
        }
        Log.i(TAG, "prepareDataSetForChart: ${sortedList.toString()}")

        sortedList?.let { list ->
            val airTSet = ArrayList<Entry>()
            val airHSet = ArrayList<Entry>()
            val soilHSet = ArrayList<Entry>()
            list.forEachIndexed { index, getMeasure ->
                airTSet.add(Entry(index.toFloat(), getMeasure!!.airTemp.toFloat()))
                airHSet.add(Entry(index.toFloat(), getMeasure.airHum.toFloat()))
                soilHSet.add(Entry(index.toFloat(), getMeasure.soilHum.toFloat()))
            }
            val airTLine = LineDataSet(airTSet, "Air Temp").also {
                it.axisDependency = YAxis.AxisDependency.LEFT
            }
            val airHLine = LineDataSet(airHSet, "Air Hum").also {
                it.axisDependency = YAxis.AxisDependency.LEFT
            }
            val soilHLine = LineDataSet(soilHSet, "Soil Hum").also {
                it.axisDependency = YAxis.AxisDependency.LEFT
            }

            val lines = ArrayList<ILineDataSet>()
            lines.add(airTLine)
            lines.add(airHLine)
            lines.add(soilHLine)
            return lines
        }
        return null
    }

    companion object {
        const val TAG = "HOMEVIEWMODEL"
    }
}