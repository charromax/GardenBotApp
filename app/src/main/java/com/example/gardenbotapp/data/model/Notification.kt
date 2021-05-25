/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.model

import com.example.gardenbotapp.R
import java.util.*

data class Notification(
    val id: String? = UUID.randomUUID().toString(),
    val createdAt: String? = "",
    val code: Int? = -1,
    val message: String? = "",
) {

    val image = when (code) {
        101 -> R.drawable.ic_internet_of_things
        102 -> R.drawable.ic_weather
        else -> -1
    }
}

