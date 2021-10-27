/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.autopilot_parameters

import androidx.compose.runtime.mutableStateOf
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.data.domain.ParametersRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.model.AutoPilotParamsPayload
import com.example.gardenbotapp.data.model.Parameters
import com.example.gardenbotapp.di.ApplicationIoScope
import com.example.gardenbotapp.type.Params
import com.example.gardenbotapp.type.StatusRequest
import com.example.gardenbotapp.ui.autopilot_parameters.composables.HourPickerType
import com.example.gardenbotapp.ui.autopilot_parameters.composables.VentilationMode
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import com.example.gardenbotapp.ui.home.sections.deviceorders.OrderType
import com.example.gardenbotapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

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

    val auto_pilot_mode = mutableStateOf("")
    val min_hum = mutableStateOf(0)
    val max_hum = mutableStateOf(0)
    val min_soil = mutableStateOf(0)
    val max_soil = mutableStateOf(0)
    val min_temp = mutableStateOf(0)
    val max_temp = mutableStateOf(0)
    val hour_on = mutableStateOf(0)
    val hour_off = mutableStateOf(0)
    val cycle_on = mutableStateOf(0)
    val cycle_off = mutableStateOf(0)

    var currentParams = Parameters()

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
                            currentParams =
                                Parameters.fromParams(data.newAutoPilotParamsResponse.params)
                            loadCurrentParams(currentParams)
                        }
                    }
                }
        }
    }

    private fun loadCurrentParams(updateParams: Parameters) {
        scope.launch {
            min_hum.value = updateParams.min_hum
            max_hum.value = updateParams.max_hum
            min_temp.value = updateParams.min_temp
            max_temp.value = updateParams.max_temp
            min_soil.value = updateParams.min_soil
            max_soil.value = updateParams.max_soil
            cycle_off.value = updateParams.cycle_off
            cycle_on.value = updateParams.cycle_on
            hour_on.value = updateParams.hour_on
            hour_off.value = updateParams.hour_off
            auto_pilot_mode.value = updateParams.auto_pilot_mode
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
                    Params(
                        auto_pilot_mode.value,
                        min_hum.value,
                        max_hum.value,
                        min_soil.value,
                        max_soil.value,
                        min_temp.value,
                        max_temp.value,
                        hour_on.value,
                        hour_off.value,
                        cycle_on.value,
                        cycle_off.value
                    )
                )
            )
        }
    }


    /**
     * set initial state for parameters sliders
     */
    fun getEndTemperatureValue(obtainedParams: Parameters) =
        obtainedParams.max_temp.convertToAirTempFloat()

    fun getStartTemperature(obtainedParams: Parameters) =
        obtainedParams.min_temp.convertToAirTempFloat()


    fun getEndSoilHumidity(obtainedParams: Parameters) =
        obtainedParams.max_soil.convertTosoilHumFloat()


    fun getStartSoilHumidity(obtainedParams: Parameters) =
        obtainedParams.min_soil.convertTosoilHumFloat()


    fun getEndAirHumidity(obtainedParams: Parameters) =
        (obtainedParams.max_hum.convertToAirHumFloat())


    fun getStartAirHumidity(obtainedParams: Parameters) =
        (obtainedParams.min_hum.convertToAirHumFloat())

    /**
     * updates lamp's hour on and off
     */
    fun updateLampCycle(
        hour: Int,
        type: HourPickerType
    ) {
        when (type) {
            HourPickerType.ON -> {
                hour_on.value = hour
            }
            HourPickerType.OFF -> {
                hour_off.value = hour
            }
        }
    }

    /**
     * update parameters from sliders
     */
    fun updateSelectedRange(slider: AutoPilotParams, range: ClosedFloatingPointRange<Float>) {
        scope.launch {
            when (slider) {
                AutoPilotParams.TEMP -> {
                    min_temp.value =
                        range.start.shorten(1).convertToTemperature().roundToInt()
                    max_temp.value =
                        range.endInclusive.shorten(1).convertToTemperature().roundToInt()
                }
                AutoPilotParams.SOIL_HUM -> {
                    min_soil.value =
                        range.start.shorten(1).convertToSoilHumidityPercent().roundToInt()
                    max_soil.value =
                        range.endInclusive.shorten(1).convertToSoilHumidityPercent().roundToInt()
                }
                AutoPilotParams.AIR_HUM -> {
                    min_hum.value =
                        range.start.shorten(1).convertToAirHumidityPercent().roundToInt()
                    max_hum.value =
                        range.endInclusive.shorten(1).convertToAirHumidityPercent().roundToInt()
                }
            }
        }
    }

    fun updateVentilationMode(newMode: VentilationMode) {
        auto_pilot_mode.value = newMode.name
    }

    fun updateVentCycle(newCycle: Float) {
        cycle_on.value = newCycle.convertToVentilationCycle()
        cycle_off.value = cycle_on.value.getVentilationCycleOff()
    }

    sealed class ParametersEvents {
        data class OnParamsUpdateError(val message: String) : ParametersEvents()
        data class OnParamsUpdateSent(val message: String) : ParametersEvents()
    }
}
