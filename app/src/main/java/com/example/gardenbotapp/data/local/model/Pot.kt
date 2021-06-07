/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.local.model

import com.example.gardenbotapp.util.Material
import com.example.gardenbotapp.util.Substrate

data class Pot(
    val material: Material,
    val size: Int,
    val soil: Substrate
)