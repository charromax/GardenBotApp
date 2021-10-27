package com.example.gardenbotapp.ui.autopilot_parameters.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gardenbotapp.R
import com.example.gardenbotapp.util.convertToVentilationFloat
import java.util.*

enum class VentilationMode { AUTO, MANUAL }

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun VentilationModeSelector(
    mode: VentilationMode,
    timeOn: Int,
    onModeChanged: (VentilationMode) -> Unit,
    onVentilationTimeSet: (Float) -> Unit
) {
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
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
            text = stringResource(
                R.string.vent_mode,
                mode.name.uppercase(Locale.getDefault())
            ),
        )
        Row(
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                modifier = Modifier.padding(horizontal = 4.dp),
                checked = mode == VentilationMode.AUTO,
                onCheckedChange = {
                    onModeChanged.invoke(if (mode == VentilationMode.AUTO) VentilationMode.MANUAL else VentilationMode.AUTO)
                })
            AnimatedVisibility(visible = mode == VentilationMode.MANUAL) {
                Slider(
                    modifier = Modifier.padding(start = 16.dp, end = 0.dp),
                    value = timeOn.convertToVentilationFloat(),
                    onValueChange = {
                        onVentilationTimeSet(it)
                    })
            }
        }
    }

}
