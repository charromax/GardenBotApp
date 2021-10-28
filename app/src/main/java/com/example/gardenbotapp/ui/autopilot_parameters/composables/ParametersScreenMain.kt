package com.example.gardenbotapp.ui.autopilot_parameters.composables

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gardenbotapp.R
import com.example.gardenbotapp.ui.autopilot_parameters.AutoPilotParams
import com.example.gardenbotapp.ui.autopilot_parameters.ParametersViewModel
import com.example.gardenbotapp.util.convertToAirHumFloat
import com.example.gardenbotapp.util.convertToAirTempFloat
import com.example.gardenbotapp.util.convertToVentMode
import com.example.gardenbotapp.util.convertTosoilHumFloat

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun ParametersScreenMain(paramsViewModel: ParametersViewModel = viewModel(), context: Context) {
    val auto_pilot_mode = paramsViewModel.auto_pilot_mode.value
    val min_hum = paramsViewModel.min_hum.value
    val max_hum = paramsViewModel.max_hum.value
    val min_soil = paramsViewModel.min_soil.value
    val max_soil = paramsViewModel.max_soil.value
    val min_temp = paramsViewModel.min_temp.value
    val max_temp = paramsViewModel.max_temp.value
    val hour_on = paramsViewModel.hour_on.value
    val hour_off = paramsViewModel.hour_off.value
    val cycle_on = paramsViewModel.cycle_on.value
    val cycle_off = paramsViewModel.cycle_off.value
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.params_screen_title),
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.params_screen_subtitle),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.secondary
            )
            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .padding(bottom = 4.dp)
                    .background(MaterialTheme.colors.secondaryVariant)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colors.surface)
                        .border(1.dp, Color.LightGray, MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    ParameterSlider(
                        stringResource(R.string.air_temp_parameters), AutoPilotParams.TEMP,
                        min_temp.convertToAirTempFloat(),
                        max_temp.convertToAirTempFloat()
                    ) { range ->
                        paramsViewModel.updateSelectedRange(AutoPilotParams.TEMP, range)
                    }
                    ParameterSlider(
                        stringResource(R.string.air_hum_parameters),
                        AutoPilotParams.AIR_HUM,
                        min_hum.convertToAirHumFloat(),
                        max_hum.convertToAirHumFloat()
                    ) { range ->
                        paramsViewModel.updateSelectedRange(AutoPilotParams.AIR_HUM, range)
                    }
                    ParameterSlider(
                        stringResource(R.string.soil_hum_parameters),
                        AutoPilotParams.SOIL_HUM,
                        min_soil.convertTosoilHumFloat(),
                        max_soil.convertTosoilHumFloat(),
                    ) { range ->
                        paramsViewModel.updateSelectedRange(AutoPilotParams.SOIL_HUM, range)
                    }
                }
                VentilationModeSelector(auto_pilot_mode.convertToVentMode(),
                    cycle_on, { newMode ->
                        paramsViewModel.updateVentilationMode(newMode)
                    },
                    { timeOn ->
                        paramsViewModel.updateVentCycle(timeOn)
                    })
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HourPicker(
                        context = context,
                        title = stringResource(R.string.time_lamp_on),
                        time = hour_on,
                        modifier = Modifier
                            .fillMaxWidth(.5f)
                            .padding(end = 2.dp),
                        onTimeChanged = {
                            paramsViewModel.updateLampCycle(it, HourPickerType.ON)
                        }
                    )
                    HourPicker(
                        context = context,
                        title = stringResource(R.string.time_lamp_off),
                        time = hour_off,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 2.dp),
                        onTimeChanged = {
                            paramsViewModel.updateLampCycle(it, HourPickerType.OFF)
                        }
                    )
                }
            }
        }
        Button(
            onClick = { sendParamsToServer(paramsViewModel) },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.update_gardenbot),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}


private fun sendParamsToServer(paramsViewModel: ParametersViewModel) {
    paramsViewModel.requestUpdateParams()
}

