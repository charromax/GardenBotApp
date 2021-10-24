package com.example.gardenbotapp.ui.autopilot_parameters.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.remote.model.VentilationCycleParams
import java.util.*

enum class VentilationMode { AUTO, MANUAL }

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun VentilationModeSelector(
    mode: VentilationMode, cycle: VentilationCycleParams?
) {
    val ventMode = remember { mutableStateOf(mode) }
    val timeVentOn = remember { mutableStateOf(cycle?.cycleOn ?: 0.5f) }
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .fillMaxWidth()
            .height(70.dp)
            .background(color = MaterialTheme.colors.surface)
            .border(1.dp, Color.Companion.LightGray, MaterialTheme.shapes.small)
    ) {
        LabelText(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 16.dp),
            text = stringResource(R.string.vent_mode, ventMode.value.name.uppercase(Locale.getDefault())),
        )
        Row(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                modifier = Modifier.padding(horizontal = 4.dp),
                checked = ventMode.value == VentilationMode.AUTO,
                onCheckedChange = {
                    ventMode.value = if (ventMode.value == VentilationMode.AUTO) VentilationMode.MANUAL else VentilationMode.AUTO

                })
            DisappearingSlider(ventMode.value == VentilationMode.MANUAL, timeVentOn)
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun DisappearingSlider(value: Boolean, timeVentOn: MutableState<Float>) {
    if(value) {
        AnimatedVisibility(visible = value) {
            Slider(
                modifier = Modifier.padding(start = 16.dp, end = 0.dp),
                value = timeVentOn.value,
                onValueChange = { timeVentOn.value = it })
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview
@Composable
fun ComposablePreview() {
    VentilationModeSelector(cycle = VentilationCycleParams.fromInt(5),
        mode = VentilationMode.MANUAL
    )
}