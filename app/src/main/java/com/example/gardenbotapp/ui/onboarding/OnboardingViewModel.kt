/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.onboarding

import android.util.Log
import androidx.lifecycle.*
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.NewDeviceSubscription
import com.example.gardenbotapp.data.domain.OnboardingRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.remote.model.Device
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private val DEV_NAME = "deviceName"


@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : GardenBotBaseViewModel() {

    private val _deviceName = MutableLiveData<String>()
    val deviceName: LiveData<String> get() = _deviceName

    private val _deviceId = MutableLiveData<String>()
    val deviceId: LiveData<String> get() = _deviceId

    private val _subDevice = MutableLiveData<Device>()
    val subDevice: LiveData<Device> get() = _subDevice

    private val _onboardingEventsChannel = Channel<OnboardingEvents>()
    val onboardEvents = _onboardingEventsChannel.receiveAsFlow()

    fun setDeviceName(name: String) {
        _deviceName.value = name
    }

    fun subscribeToDevice(devName: String) {
        viewModelScope.launch {
            try {
                _onboardingEventsChannel.send(OnboardingEvents.DeviceSuscriptionStart)
                onboardingRepository.newDeviceSub(devName)
                    .retryWhen { _, attempt ->
                        delay((attempt * 1000))    //exp delay
                        true
                    }
                    .collect { res ->
                        if (res.hasErrors() || res.data?.newDevice == null) {
                            _onboardingEventsChannel.send(OnboardingEvents.OnboardingError("ERROR: ${res.errors?.map { it.message }}"))
                        } else {
                            onDeviceFound(res)
                        }
                    }
            } catch (e: ApolloException) {
                _onboardingEventsChannel.send(OnboardingEvents.OnboardingError("ERROR: ${e.message}"))
            }
        }
    }

    private fun onDeviceFound(res: Response<NewDeviceSubscription.Data>) {
        viewModelScope.launch { _onboardingEventsChannel.send(OnboardingEvents.DeviceFoundEvent) }
        with(res.data!!.newDevice) {
            Log.i(TAG, "subscribeToDevice: device found")
            _subDevice.value = Device(
                id = id,
                deviceName = deviceName,
                createdAt = createdAt
            )

        }
    }


    fun activateDevice(devName: String, token: String? = null) {
        viewModelScope.launch {
            val currentToken = token ?: preferencesManager.tokenFlow.first()
            val currentUser = preferencesManager.userIdFlow.first()
            if (devName.isBlank()) {
                _onboardingEventsChannel.send(
                    OnboardingEvents.EmptyDeviceNameError
                )
                return@launch
            } else {
                try {
                    _deviceId.value =
                        onboardingRepository.activateDevice(devName, currentUser, currentToken)
                } catch (e: ApolloException) {
                    e.message?.let { message ->
                        onErrorMessage(message, currentToken, devName)
                    }
                }
            }
        }
    }

    private suspend fun CoroutineScope.onErrorMessage(
        message: String,
        currentToken: String,
        devName: String
    ) {
        if (message.contains("Invalid/Expired token")) {
            refreshTokenAndRetry(currentToken, devName)
        } else {
            _onboardingEventsChannel.send(OnboardingEvents.OnboardingError(message))
        }
    }

    private suspend fun CoroutineScope.refreshTokenAndRetry(
        currentToken: String,
        devName: String
    ) {
        try {
            Log.i(TAG, "refresh token...")
            val res = async { refreshToken(currentToken) }
            val newToken = res.await()
            preferencesManager.updateToken(newToken?.refreshToken)
            activateDevice(devName, newToken?.refreshToken)
        } catch (e: ApolloException) {
            _onboardingEventsChannel.send(OnboardingEvents.TokenError(e.message))
            preferencesManager.updateToken("")
        }
    }


    fun onDeviceActivated(devId: String) {
        viewModelScope.launch {
            preferencesManager.updateDevice(devId)
            _onboardingEventsChannel.send(OnboardingEvents.OnboardingSuccess)
        }
    }


    sealed class OnboardingEvents {
        data class OnboardingError(val message: String) : OnboardingEvents()
        object EmptyDeviceNameError : OnboardingEvents()
        object DeviceFoundEvent : OnboardingEvents()
        object OnboardingSuccess : OnboardingEvents()
        object DeviceSuscriptionStart : OnboardingEvents()
        data class TokenError(val message: String?) : OnboardingEvents()
    }

    companion object {
        const val TAG = "ONBOARDING"
    }

}