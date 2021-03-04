/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.onboarding

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.GardenBotRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val gardenBotRepository: GardenBotRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var deviceName = state.get<String>("deviceName") ?: ""
        set(value) {
            field = value
            state.set("deviceName", value)
        }

    private val _deviceId = MutableLiveData<String>()
    val deviceId: LiveData<String> get() = _deviceId

    private val onboardingEventsChannel = Channel<OnboardingEvents>()
    val onboardEvents = onboardingEventsChannel.receiveAsFlow()

    fun activateDevice(token: String? = null) {
        viewModelScope.launch {
            val currentToken = token ?: preferencesManager.tokenFlow.first()
            val currentUser = preferencesManager.userIdFlow.first()
            if (deviceName.isBlank()) {
                onboardingEventsChannel.send(
                    OnboardingEvents.OnboardingError(
                        context.getString(
                            R.string.empty_fields_message
                        )
                    )
                )
                return@launch

            } else {
                try {
                    _deviceId.value =
                        gardenBotRepository.activateDevice(deviceName, currentUser, currentToken)

                } catch (e: ApolloException) {
                    e.message?.let { message ->
                        if (message.contains("Invalid/Expired token")) {
                            try {
                                Log.i(TAG, "refresh token...")
                                val res = async { gardenBotRepository.refreshToken(currentToken) }
                                val newToken = res.await()
                                preferencesManager.updateToken(newToken?.refreshToken)
                                activateDevice(newToken?.refreshToken)
                            } catch (e: ApolloException) {
                                onboardingEventsChannel.send(OnboardingEvents.TokenError(e.message))
                                preferencesManager.updateToken("")
                            }
                        } else {
                            onboardingEventsChannel.send(OnboardingEvents.OnboardingError(message))
                        }
                    }
                }
            }
        }
    }

    fun onDeviceActivated(devId: String) {
        viewModelScope.launch {
            preferencesManager.updateDevice(devId)
            onboardingEventsChannel.send(OnboardingEvents.OnboardingSuccess)
        }
    }


    sealed class OnboardingEvents {
        data class OnboardingError(val message: String) : OnboardingEvents()
        object OnboardingSuccess : OnboardingEvents()
        data class TokenError(val message: String?) : OnboardingEvents()
    }

    companion object {
        const val TAG = "ACTIVATE"
    }

}