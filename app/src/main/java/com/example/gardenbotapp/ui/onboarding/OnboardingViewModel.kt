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
import com.example.gardenbotapp.data.model.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private val DEV_NAME = "deviceName"


@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val gardenBotRepository: GardenBotRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    init {
        Log.i(TAG, "Owner: ${this.context}")
    }

    private val _deviceName = MutableLiveData<String>()
    val deviceName: LiveData<String> get() = _deviceName

    private val _deviceId = MutableLiveData<String>()
    val deviceId: LiveData<String> get() = _deviceId

    private val _subDevice = MutableLiveData<Device>()
    val subDevice: LiveData<Device> get() = _subDevice

    private val onboardingEventsChannel = Channel<OnboardingEvents>()
    val onboardEvents = onboardingEventsChannel.receiveAsFlow()

    fun setDeviceName(name: String) {
        _deviceName.value = name
    }

    fun subscribeToDevice(devName: String) {
        viewModelScope.launch {
            try {
                onboardingEventsChannel.send(OnboardingEvents.DeviceSuscriptionStart)
                gardenBotRepository.newDeviceSub(devName)
                    .retryWhen { _, attempt ->
                        delay((attempt * 1000))    //exp delay
                        true
                    }
                    .collect { res ->
                        if (res.hasErrors() || res.data?.newDevice == null) {
                            onboardingEventsChannel.send(OnboardingEvents.OnboardingError("ERROR: ${res.errors?.map { it.message }}"))
                        } else {
                            with(res.data!!.newDevice) {
                                Log.i(TAG, "subscribeToDevice: device found")
                                _subDevice.value = Device(
                                    id = id,
                                    deviceName = deviceName,
                                    createdAt = createdAt
                                )

                            }
                        }
                    }
            } catch (e: ApolloException) {
                onboardingEventsChannel.send(OnboardingEvents.OnboardingError("ERROR: ${e.message}"))
            }
        }
    }

    fun activateDevice(devName: String, token: String? = null) {
        viewModelScope.launch {
            val currentToken = token ?: preferencesManager.tokenFlow.first()
            val currentUser = preferencesManager.userIdFlow.first()
            if (devName.isBlank()) {
                onboardingEventsChannel.send(
                    OnboardingEvents.OnboardingError(
                        context.getString(R.string.error_empty_device_name)
                    )
                )
                return@launch
            } else {
                try {
                    _deviceId.value =
                        gardenBotRepository.activateDevice(devName, currentUser, currentToken)

                } catch (e: ApolloException) {
                    e.message?.let { message ->
                        if (message.contains("Invalid/Expired token")) {
                            try {
                                Log.i(TAG, "refresh token...")
                                val res = async { gardenBotRepository.refreshToken(currentToken) }
                                val newToken = res.await()
                                preferencesManager.updateToken(newToken?.refreshToken)
                                activateDevice(devName, newToken?.refreshToken)
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
        object DeviceSuscriptionStart : OnboardingEvents()
        data class TokenError(val message: String?) : OnboardingEvents()
    }

    companion object {
        const val TAG = "ONBOARDING"
    }

}