/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.local.model

import com.example.gardenbotapp.util.Origin
import com.example.gardenbotapp.util.PlantType

data class Plant(
    val id: String,
    val type: PlantType,
    val name: String,
    val createdAt: String,
    val origin: Origin,
    val pot: Pot
)