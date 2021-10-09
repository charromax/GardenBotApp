/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.autopilot_parameters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RangeSlider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentAutopilotParamsBinding
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.example.gardenbotapp.util.*
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

enum class AutoPilotParams {
    TEMP, SOIL_HUM, AIR_HUM
}

@AndroidEntryPoint
class ParametersFragment :
    GardenbotBaseFragment<FragmentAutopilotParamsBinding, ParametersViewModel>() {

    override fun getViewModelClass() = ParametersViewModel::class.java
    override fun getViewBinding() = FragmentAutopilotParamsBinding.inflate(layoutInflater)

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.composeFragContent.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MdcTheme {
                    ParametersScreenMain()
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}

@ExperimentalMaterialApi
@Composable
fun ParametersScreenMain(viewModel: ParametersViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            ParameterSlider(stringResource(R.string.air_temp_parameters), AutoPilotParams.TEMP)
            ParameterSlider(stringResource(R.string.air_hum_parameters), AutoPilotParams.AIR_HUM)
            ParameterSlider(stringResource(R.string.soil_hum_parameters), AutoPilotParams.SOIL_HUM)
        }

    }
}

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

@ExperimentalMaterialApi
@Preview
@Composable
fun ComposablePreview() {
    ParametersScreenMain()
}
