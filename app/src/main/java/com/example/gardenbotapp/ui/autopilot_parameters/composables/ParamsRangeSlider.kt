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
fun ParameterSlider(label: String, type: AutoPilotParams, start: Float, end: Float, onParamsRangeChanged:(ClosedFloatingPointRange<Float>)->Unit) {
    val positions = when (type) {
        AutoPilotParams.TEMP -> "${
            start.convertToTemperature().toString()
                .toTemperatureString()
        } - ${
            end.convertToTemperature().toString()
                .toTemperatureString()
        }"
        AutoPilotParams.SOIL_HUM -> "${
            start.convertToSoilHumidityPercent().toString()
                .toPercentString()
        } - ${
            end.convertToSoilHumidityPercent().toString()
                .toPercentString()
        }"
        AutoPilotParams.AIR_HUM -> "${
            start.convertToAirHumidityPercent().toString()
                .toPercentString()
        } - ${
            end.convertToAirHumidityPercent().toString()
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
    RangeSlider(values = (start..end), onValueChange = {
        onParamsRangeChanged.invoke(it)
    })
}