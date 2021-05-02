/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.deviceorders

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.data.GardenBotRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.type.Order
import com.example.gardenbotapp.type.Payload
import com.example.gardenbotapp.ui.home.sections.chart.EXP_TOKEN
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

enum class OrderType { MANUAL, SETTINGS }

class OrdersViewModel @Inject constructor(
    private val gardenBotRepository: GardenBotRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : ViewModel() {
    private val ordersEventsChannel = Channel<OrdersEvents>()
    val ordersEvents = ordersEventsChannel.receiveAsFlow()

    fun refreshDeviceState() {
        //TODO: retrieve device state from Gardenbot and update UI accordingly
    }


    fun sendOrder(devicePin: Int, action: Boolean, token: String? = null) {
        val currentToken = token ?: runBlocking { preferencesManager.tokenFlow.first() }
        viewModelScope.launch {
            val deviceId = preferencesManager.deviceIdFlow.first()
            try {
                val response = gardenBotRepository.sendMqttOrder(
                    Payload(
                        deviceId,
                        OrderType.MANUAL.name,
                        Order(action, devicePin)
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
                        ordersEventsChannel.send(OrdersEvents.OnOrderError(message))
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
    }

    companion object {
        const val TAG = "OrdersViewModel"
    }

}