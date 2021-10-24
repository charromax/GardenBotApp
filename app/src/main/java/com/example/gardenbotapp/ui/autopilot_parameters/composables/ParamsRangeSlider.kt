package com.example.gardenbotapp.ui.autopilot_parameters.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RangeSlider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gardenbotapp.ui.autopilot_parameters.AutoPilotParams
import com.example.gardenbotapp.util.*

@ExperimentalMaterialApi
@Composable
fun ParameterSlider(label: String, type: AutoPilotParams) {
    val sliderPosition = remember { mutableStateOf(0.1f..0.7f) }
    val positions = when (type) {
        AutoPilotParams.TEMP -> "${
            sliderPosition.value.start.shorten(1).convertToTemperature().toString()
                .toTemperatureString()
        } - ${
            sliderPosition.value.endInclusive.shorten(1).convertToTemperature().toString()
                .toTemperatureString()
        }"
        AutoPilotParams.SOIL_HUM -> "${
            sliderPosition.value.start.shorten(1).convertToSoilHumidityPercent().toString()
                .toPercentString()
        } - ${
            sliderPosition.value.endInclusive.shorten(1).convertToSoilHumidityPercent().toString()
                .toPercentString()
        }"
        AutoPilotParams.AIR_HUM -> "${
            sliderPosition.value.start.shorten(1).convertToAirHumidityPercent().toString()
                .toPercentString()
        } - ${
            sliderPosition.value.endInclusive.shorten(1).convertToAirHumidityPercent().toString()
                .toPercentString()
        }"
    }

    Text(
        text = label, modifier = Modifier
            .padding(bottom = 4.dp),
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.subtitle2
    )
    Text(
        positions,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp
    )
    RangeSlider(values = sliderPosition.value, onValueChange = {
        sliderPosition.value = it
    })
}