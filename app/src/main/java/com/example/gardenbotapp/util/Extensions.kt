/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.util

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    val snack = Snackbar.make(this, message, duration)
    snack.show()
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.toDateFormat(): String {
    val parser = DateTimeFormatter.ISO_DATE_TIME
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    return LocalDateTime.parse(this, parser).format(formatter)
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enable: Boolean) {
    isEnabled = enable
    alpha = if (enable) 1f else .5f
}

fun getInflatedViewHolder(
    parent: ViewGroup,
    layoutID: Int
): View {
    return LayoutInflater.from(parent.context).inflate(layoutID, parent, false)
}

fun String.toDate(
    dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss",
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun String.toTemperatureString(): String {
    return "${this}°C"
}