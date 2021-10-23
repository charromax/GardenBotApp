/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.autopilot_parameters

import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.NewAutoPilotParamsResponseSubscription
import com.example.gardenbotapp.data.domain.ParametersRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.remote.model.AutoPilotParams
import com.example.gardenbotapp.di.ApplicationIoScope
import com.example.gardenbotapp.type.StatusRequest
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import com.example.gardenbotapp.ui.home.sections.deviceorders.OrderType
import com.example.gardenbotapp.ui.home.sections.deviceorders.OrdersViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val autoPilotRepository: ParametersRepository
) : GardenBotBaseViewModel() {
    @Inject
    @ApplicationIoScope
    lateinit var scope: CoroutineScope

    private val _eventsChannel = Channel<ParametersEvents>()
    val eventsChannel = _eventsChannel.receiveAsFlow()

    init {
        requestCurrentParams()
        getCurrentParams()
    }

    private fun requestCurrentParams() {
        scope.launch {
            val currentToken = async { preferencesManager.tokenFlow.first() }
            val deviceId = async { preferencesManager.deviceIdFlow.first() }
            try {
                val response = autoPilotRepository.sendParamsStatusRequest(
                    StatusRequest(deviceId.await(), OrderType.PARAMS_STATUS_REQUEST.name),
                    currentToken.await()
                )
                response?.let { _eventsChannel.send(ParametersEvents.OnParamsUpdateSent(it)) }

            } catch (e: ApolloException) {
                e.message?.let { message ->
                    _eventsChannel.send(ParametersEvents.OnParamsUpdateError(message))
                }
            }
        }
    }

    private fun getCurrentParams() {
        scope.launch {
            val deviceId = preferencesManager.deviceIdFlow.first()
            autoPilotRepository.paramsStatusResponseSub(deviceId)
                .retryWhen { _, attempt ->
                    delay((attempt * 1000))    //exp delay
                    true
                }
                .collect { res ->
                    if (res.hasErrors() || res.data?.newAutoPilotParamsResponse == null) {
                        _eventsChannel.send(ParametersEvents.OnParamsUpdateError("ERROR: ${res.errors?.map { it.message }}"))
                    } else {
                        res.data?.let { data ->
                            _eventsChannel.send(
                                ParametersEvents.OnParametersUpdateReceived(
                                    data.newAutoPilotParamsResponse.params
                                )
                            )
                        }
                    }
                }
        }
    }

    fun updateCurrentParams(payload: AutoPilotParams) {
        scope.launch {
            val currentToken = async { preferencesManager.tokenFlow.first() }
            val params = async { payload.toParamsPayload(preferencesManager) }
            try {
                val response =
                    autoPilotRepository.changeParams(params.await(), currentToken.await())
                response?.let { _eventsChannel.send(ParametersEvents.OnParamsUpdateSent(it)) }

            } catch (e: ApolloException) {
                e.message?.let { message ->
                    _eventsChannel.send(ParametersEvents.OnParamsUpdateError(message))
                }
            }
        }
    }

    sealed class ParametersEvents {
        data class OnParametersUpdateReceived(val newParams: NewAutoPilotParamsResponseSubscription.Params) :
            ParametersEvents()
        data class OnParamsUpdateError(val message: String) : ParametersEvents()
        data class OnParamsUpdateSent(val message: String) : ParametersEvents()
    }
}