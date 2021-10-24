package com.example.gardenbotapp.ui.autopilot_parameters.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RangeSlider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gardenbotapp.ui.autopilot_parameters.AutoPilotParams
import com.example.gardenbotapp.ui.autopilot_parameters.ParametersViewModel
import com.example.gardenbotapp.ui.home.sections.chart.*
import com.example.gardenbotapp.util.*
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@Composable
fun ParameterSlider(label: String, type: AutoPilotParams, viewModel: ParametersViewModel) {
    val paramsState by viewModel.paramsObtained.collectAsState()
    var updateStart = 0
    var updateEnd = 0
    var sliderValueLabel = ""
    lateinit var sliderPosition: MutableState<ClosedFloatingPointRange<Float>>
    when (type) {
        AutoPilotParams.TEMP -> {
            sliderPosition =
                remember { mutableStateOf((paramsState.min_temp / MIN_ALLOWED_TEMPERATURE).toFloat()..(paramsState.max_temp / MAX_ALLOWED_TEMPERATURE).toFloat()) }
            updateStart = sliderPosition.value.start.shorten(1).convertToTemperature().roundToInt()
            updateEnd =
                sliderPosition.value.endInclusive.shorten(1).convertToTemperature().roundToInt()
            sliderValueLabel = "${updateStart.toString().toTemperatureString()} - ${
                updateEnd.toString().toTemperatureString()
            }"
        }
        AutoPilotParams.SOIL_HUM -> {
            sliderPosition =
                remember { mutableStateOf((paramsState.min_soil / MIN_ALLOWED_SOIL_HUMIDITY).toFloat()..(paramsState.max_soil / MAX_ALLOWED_SOIL_HUMIDITY).toFloat()) }
            updateStart =
                sliderPosition.value.start.shorten(1).convertToSoilHumidityPercent().roundToInt()
            updateEnd = sliderPosition.value.endInclusive.shorten(1).convertToSoilHumidityPercent()
                .roundToInt()
            sliderValueLabel = "${updateStart.toString().toPercentString()} - ${
                updateEnd.toString().toPercentString()
            }"
        }
        AutoPilotParams.AIR_HUM -> {
            sliderPosition =
                remember { mutableStateOf((paramsState.min_hum / MIN_ALLOWED_AIR_HUMIDITY).toFloat()..(paramsState.max_hum / MAX_ALLOWED_AIR_HUMIDITY).toFloat()) }
            updateStart =
                sliderPosition.value.start.shorten(1).convertToAirHumidityPercent().roundToInt()
            updateEnd = sliderPosition.value.endInclusive.shorten(1).convertToAirHumidityPercent()
                .roundToInt()
            sliderValueLabel = "${updateStart.toString().toPercentString()} - ${
                updateEnd.toString().toPercentString()
            }"
        }
    }

    Text(
        text = label, modifier = Modifier
            .padding(bottom = 4.dp),
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.subtitle2
    )
    Text(
        sliderValueLabel,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp
    )
    RangeSlider(values = sliderPosition.value, onValueChange = {
        sliderPosition.value = it
        updateViewModelParams(updateStart, updateEnd, viewModel, type)
    })
}

private fun updateViewModelParams(
    start: Int,
    end: Int,
    viewModel: ParametersViewModel,
    type: AutoPilotParams
) {
    when (type) {
        AutoPilotParams.TEMP -> {
            viewModel.updateParams.min_temp = start
            viewModel.updateParams.max_temp = end
        }
        AutoPilotParams.SOIL_HUM -> {
            viewModel.updateParams.min_soil = start
            viewModel.updateParams.max_soil = end
        }
        AutoPilotParams.AIR_HUM -> {
            viewModel.updateParams.min_hum = start
            viewModel.updateParams.max_hum = end
        }
    }
}
