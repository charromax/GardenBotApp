package com.example.gardenbotapp.ui.autopilot_parameters.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gardenbotapp.R
import java.util.*

@Composable
fun LabelText(text:String, modifier: Modifier) {
    Text(
        modifier = modifier,
        text = text,
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.subtitle2
    )
}