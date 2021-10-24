package com.example.gardenbotapp.ui.autopilot_parameters.composables

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gardenbotapp.R
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourPicker(context: Context, title: String, modifier: Modifier = Modifier) {
    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    val time = remember { mutableStateOf("") }
    val timePickedDialog = TimePickerDialog(
        context,
        { _, hour: Int, _ ->
            time.value = "$hour"
        }, hour, minute, true
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(color = MaterialTheme.colors.surface)
            .border(1.dp, Color.Companion.LightGray, MaterialTheme.shapes.small)
            .padding(vertical = 4.dp, horizontal = 16.dp)
    ) {
        LabelText(
            text = title,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
            Text(
                text = time.value,
                modifier = Modifier.padding( start = 16.dp),
                style = TextStyle(
                    color = MaterialTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun HourPreview() {
    HourPicker(LocalContext.current, stringResource(R.string.time_lamp_on))
}