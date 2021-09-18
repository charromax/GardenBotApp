/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.deviceorders

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.data.domain.ManualControlRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.type.Order
import com.example.gardenbotapp.type.Payload
import com.example.gardenbotapp.type.StatusRequest
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import com.example.gardenbotapp.ui.home.sections.chart.EXP_TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import javax.inject.Inject

enum class OrderType { MANUAL, SETTINGS, STATUS_REQUEST }
enum class ConnectedDevice(val pin: Int) {
    LAMPARA(0), VENTILADOR(1), EXTRACTOR(2), INTRACTOR(3)
}

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val manualControlRepository: ManualControlRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : GardenBotBaseViewModel() {
    private val ordersEventsChannel = Channel<OrdersEvents>()
    val ordersEvents = ordersEventsChannel.receiveAsFlow()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        subscribeToDeviceStatusResponse()
    }

    fun refreshDeviceState(token: String? = null) {
        val currentToken = getOrRefreshToken(token)
        scope.launch {
            val deviceId = async { preferencesManager.deviceIdFlow.first() }
            try {
                val response = manualControlRepository.sendDeviceStatusRequst(
                    StatusRequest(deviceId.await(), OrderType.STATUS_REQUEST.name), currentToken
                )
                response?.let { ordersEventsChannel.send(OrdersEvents.OnOrderSent(it)) }

            } catch (e: ApolloException) {
                e.message?.let { message ->
                    if (message.contains(EXP_TOKEN)) {
                        tryRefreshToken(currentToken)
                    } else {
                        ordersEventsChannel.send(OrdersEvents.OnTokenError)
                    }
                }
            }
        }
    }

    private fun subscribeToDeviceStatusResponse() {
        scope.launch {
            val deviceId = preferencesManager.deviceIdFlow.first()
            manualControlRepository.statusResponseSub(deviceId)
                .retryWhen { _, attempt ->
                    delay((attempt * 1000))    //exp delay
                    true
                }
                .collect { res ->
                    if (res.hasErrors() || res.data?.newStatusResponse == null) {
                        ordersEventsChannel.send(OrdersEvents.OnOrderError("ERROR: ${res.errors?.map { it.message }}"))
                    } else {
                        res.data?.let { data ->
                            ordersEventsChannel.send(OrdersEvents.OnStatusResponseReceived(
                                data.newStatusResponse.devices.map {
                                    OnDeviceStateChanged(
                                        ConnectedDevice.values()[it!!.dev_id],
                                        it.status
                                    )
                                }
                            ))
                        }
                    }
                }
        }
    }

    fun sendOrder(selectedDevice: ConnectedDevice, action: Boolean, token: String? = null) {
        val currentToken = getOrRefreshToken(token)
        scope.launch {
            val deviceId = preferencesManager.deviceIdFlow.first()
            try {
                val response = manualControlRepository.sendMqttOrder(
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
                        tryRefreshToken(currentToken)
                    } else {
                        ordersEventsChannel.send(OrdersEvents.OnTokenError)
                    }
                }
            }

        }
    }

    private suspend fun tryRefreshToken(currentToken: String) {
        scope.launch {
            try {
                Log.i(TAG, "sendOrder: refresh token...")
                val res = async { refreshToken(currentToken) }
                val newToken = res.await()
                preferencesManager.updateToken(newToken?.refreshToken)
            } catch (e: ApolloException) {
                ordersEventsChannel.send(OrdersEvents.OnOrderError(e.message!!))
                preferencesManager.updateToken("")
            }
        }
    }

    private fun getOrRefreshToken(token: String?) =
        token ?: runBlocking { preferencesManager.tokenFlow.first() }

    fun setInitialState() {
        viewModelScope.launch {
            ordersEventsChannel.send(OrdersEvents.OnInitialState)
        }
    }

    sealed class OrdersEvents {
        object OnInitialState : OrdersEvents()
        data class OnOrderSent(val message: String) : OrdersEvents()
        data class OnOrderError(val message: String) : OrdersEvents()
        data class OnStatusResponseReceived(val devices: List<OnDeviceStateChanged>) :
            OrdersEvents()
        object OnTokenError : OrdersEvents()
    }

    data class OnDeviceStateChanged(val device: ConnectedDevice, val newState: Boolean)

    companion object {
        const val TAG = "OrdersViewModel"
    }

}