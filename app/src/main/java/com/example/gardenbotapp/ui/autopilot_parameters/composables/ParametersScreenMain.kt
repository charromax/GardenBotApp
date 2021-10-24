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
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import com.example.gardenbotapp.data.remote.model.VentilationCycleParams
import com.example.gardenbotapp.ui.autopilot_parameters.AutoPilotParams
import com.example.gardenbotapp.ui.autopilot_parameters.ParametersViewModel

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun ParametersScreenMain(paramsViewModel: ParametersViewModel = viewModel(), context: Context) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                ParameterSlider(stringResource(R.string.air_temp_parameters), AutoPilotParams.TEMP)
                ParameterSlider(
                    stringResource(R.string.air_hum_parameters),
                    AutoPilotParams.AIR_HUM
                )
                ParameterSlider(
                    stringResource(R.string.soil_hum_parameters),
                    AutoPilotParams.SOIL_HUM
                )
            }

            VentilationModeSelector(
                cycle = VentilationCycleParams.fromInt(11),
                mode = VentilationMode.MANUAL
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HourPicker(
                    context = context,
                    stringResource(R.string.time_lamp_on),
                    modifier = Modifier.padding(start = 16.dp)
                )
                HourPicker(
                    context = context, stringResource(R.string.time_lamp_off),
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
            Button(onClick = { sendParamsToServer() }, modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.update_gardenbot),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
    }
}

private fun sendParamsToServer() {
    TODO("Not yet implemented")
}

