/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home

import android.content.Context
import androidx.lifecycle.*
import com.example.gardenbotapp.MeasuresQuery
import com.example.gardenbotapp.data.GardenBotRepository
import com.example.gardenbotapp.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
        viewModelScope.launch {
            gardenBotRepository.getMeasuresForDevice("601599a09606f008af118b79").collect {
                _measures.value = it
            }
        }
    }

}