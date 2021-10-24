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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val paramsState by paramsViewModel.paramsObtained.collectAsState()
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
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                ParameterSlider(
                    stringResource(R.string.air_temp_parameters), AutoPilotParams.TEMP,
                    paramsViewModel
                )
                ParameterSlider(
                    stringResource(R.string.air_hum_parameters),
                    AutoPilotParams.AIR_HUM,
                    paramsViewModel
                )
                ParameterSlider(
                    stringResource(R.string.soil_hum_parameters),
                    AutoPilotParams.SOIL_HUM,
                    paramsViewModel
                )
            }
            VentilationModeSelector(
                cycle = VentilationCycleParams.fromInt(11),
                mode = VentilationMode.MANUAL,
                viewModel = paramsViewModel
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HourPicker(
                    context = context,
                    title = stringResource(R.string.time_lamp_on),
                    type = HourPickerType.ON,
                    viewModel = paramsViewModel
                )
                HourPicker(
                    context = context,
                    title = stringResource(R.string.time_lamp_off),
                    type = HourPickerType.ON,
                    viewModel = paramsViewModel
                )
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
}

private fun sendParamsToServer(paramsViewModel: ParametersViewModel) {
    paramsViewModel.requestUpdateParams()
}

