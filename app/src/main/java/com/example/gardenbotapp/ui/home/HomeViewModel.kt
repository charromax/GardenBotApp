/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.gardenbotapp.data.GardenBotRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.util.Errors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gardenBotRepository: GardenBotRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : ViewModel() {

    val deviceId: LiveData<String> = preferencesManager.deviceIdFlow.asLiveData()
    private val homeEventsChannel = Channel<Errors>()
    val homeEvents = homeEventsChannel.receiveAsFlow()

    companion object {
        const val TAG = "HOMEVIEWMODEL"

    }
}