/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.remote.model

data class Measure(
    val id: String,
    val createdAt: String,
    val airHum: Double,
    val airTemp: Double,
    val soilHum: Double
)