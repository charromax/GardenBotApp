/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.deviceorders

import android.util.Log
import androidx.lifecycle.*
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.remote.GardenBotRepository
import com.example.gardenbotapp.type.Order
import com.example.gardenbotapp.type.Payload
import com.example.gardenbotapp.ui.home.sections.chart.EXP_TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

enum class OrderType { MANUAL, SETTINGS }
enum class ConnectedDevice(val pin: Int) {
    LAMPARA(0), VENTILADOR(1), EXTRACTOR(2), INTRACTOR(3)
}

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val gardenBotRepository: GardenBotRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : ViewModel() {
    private val ordersEventsChannel = Channel<OrdersEvents>()
    val ordersEvents = ordersEventsChannel.receiveAsFlow()

    private val _deviceStateChanged = MutableLiveData<OnDeviceStateChanged>()
    val onDeviceStateStateChanged: LiveData<OnDeviceStateChanged> get() = _deviceStateChanged

    fun refreshDeviceState() {
        //TODO: retrieve device state from Gardenbot and update UI accordingly
    }


    fun sendOrder(selectedDevice: ConnectedDevice, action: Boolean, token: String? = null) {
        val currentToken = token ?: runBlocking { preferencesManager.tokenFlow.first() }
        viewModelScope.launch {
            val deviceId = preferencesManager.deviceIdFlow.first()
            try {
                val response = gardenBotRepository.sendMqttOrder(
                    Payload(
                        deviceId,
                        OrderType.MANUAL.name,
                        Order(action, selectedDevice.pin)
                    ), currentToken
                )
                response?.let { ordersEventsChannel.send(OrdersEvents.OnOrderSent(it)) }

            } catch (e: ApolloException) {
                e.message?.let { message ->
                    if (message.contains(EXP_TOKEN)) {
                        try {
                            Log.i(TAG, "sendOrder: refresh token...")
                            val res = async { gardenBotRepository.refreshToken(currentToken) }
                            val newToken = res.await()
                            preferencesManager.updateToken(newToken?.refreshToken)
                        } catch (e: ApolloException) {
                            ordersEventsChannel.send(OrdersEvents.OnOrderError(e.message!!))
                            preferencesManager.updateToken("")
                        }
                    } else {
                        ordersEventsChannel.send(OrdersEvents.OnTokenError)
                    }
                }
            }

        }
    }

    fun setInitialState() {
        viewModelScope.launch {
            ordersEventsChannel.send(OrdersEvents.OnInitialState)
        }
    }

    sealed class OrdersEvents {
        object OnInitialState : OrdersEvents()
        data class OnOrderSent(val message: String) : OrdersEvents()
        data class OnOrderError(val message: String) : OrdersEvents()
        object OnTokenError : OrdersEvents()
    }

    data class OnDeviceStateChanged(val device: ConnectedDevice, val newState: Boolean)

    companion object {
        const val TAG = "OrdersViewModel"
    }

}