/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.autopilot_parameters

import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.data.domain.ParametersRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.remote.model.AutoPilotParamsPayload
import com.example.gardenbotapp.data.remote.model.Parameters
import com.example.gardenbotapp.di.ApplicationIoScope
import com.example.gardenbotapp.type.StatusRequest
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import com.example.gardenbotapp.ui.home.sections.deviceorders.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel @Inject constructor(
    @ApplicationIoScope
    private val scope: CoroutineScope,
    private val preferencesManager: PreferencesManager,
    private val autoPilotRepository: ParametersRepository
) : GardenBotBaseViewModel() {

    private val _eventsChannel = Channel<ParametersEvents>()
    val eventsChannel = _eventsChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _paramsObtained = MutableStateFlow(Parameters())
    val paramsObtained: StateFlow<Parameters> = _paramsObtained

    var updateParams = Parameters()

    init {
        getCurrentParams()
        requestCurrentParams()
    }

    /**
     * send params request to server to be relayed to gardenbot
     */
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
                setLoadingState(false)
                e.message?.let { message ->
                    _eventsChannel.send(ParametersEvents.OnParamsUpdateError(message))
                }
            }
        }
    }

    /**
     * subscribes to auto pilot parameters response from gardenbot
     */
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
                        setLoadingState(false)
                        _eventsChannel.send(ParametersEvents.OnParamsUpdateError("ERROR: ${res.errors?.map { it.message }}"))
                    } else {
                        res.data?.let { data ->
                            setLoadingState(false)
                            _paramsObtained.value =
                                Parameters.fromParams(data.newAutoPilotParamsResponse.params)
                        }
                    }
                }
        }
    }

    private fun setLoadingState(state: Boolean) {
        _loadingState.value = state
    }

    private fun updateCurrentParams(payload: AutoPilotParamsPayload) {
        scope.launch {
            setLoadingState(true)
            val currentToken = async { preferencesManager.tokenFlow.first() }
            val params = async { payload.toParamsPayload(preferencesManager) }
            try {
                val response =
                    autoPilotRepository.changeParams(params.await(), currentToken.await())
                response?.let {
                    setLoadingState(false)
                    _eventsChannel.send(ParametersEvents.OnParamsUpdateSent(it))
                }
            } catch (e: ApolloException) {
                e.message?.let { message ->
                    setLoadingState(false)
                    _eventsChannel.send(ParametersEvents.OnParamsUpdateError(message))
                }
            }
        }
    }

    fun requestUpdateParams() {
        scope.launch {
            updateCurrentParams(
                AutoPilotParamsPayload(
                    type = OrderType.PARAMS_STATUS_REQUEST.name.lowercase(),
                    updateParams.toParams()
                )
            )
        }
    }

    sealed class ParametersEvents {
        data class OnParamsUpdateError(val message: String) : ParametersEvents()
        data class OnParamsUpdateSent(val message: String) : ParametersEvents()
    }
}