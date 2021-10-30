package com.example.gardenbotapp.ui.autopilot_parameters.composables

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gardenbotapp.R
import com.example.gardenbotapp.ui.autopilot_parameters.ParametersViewModel
import java.util.*

enum class HourPickerType { ON, OFF }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourPicker(
    context: Context,
    title: String,
    modifier: Modifier = Modifier,
    time: Int,
    onTimeChanged: ((Int) -> Unit)
) {
    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    val timePickedDialog = TimePickerDialog(
        context,
        { _, hour: Int, _ ->
            onTimeChanged.invoke(hour)
        }, hour, minute, true
    )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(color = MaterialTheme.colors.surface)
            .border(1.dp, Color.Companion.LightGray, MaterialTheme.shapes.small)
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        IconButton(
            onClick = { timePickedDialog.show() }, modifier = Modifier
                .background(
                    color = MaterialTheme.colors.primary,
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Icon(
                painterResource(R.drawable.ic_time),
                "hour picker",
                tint = MaterialTheme.colors.background
            )
        }
        LabelText(
            text = title,
            modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
        )
        Text(
            text = time.toString(),
            modifier = Modifier.padding(start = 4.dp),
            style = TextStyle(
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
        )
    }
}

