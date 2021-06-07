/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.util

enum class PlantType(val plantName: String, val percentage: Int = 100) {
    INDICA("Indica"),
    SATIVA("Sativa"),
    HIBRIDA40("Híbrida 40% Indica", 40),
    HIBRIDA50("Híbrida 50% Indica", 50),
    HIBRIDA60("Híbrida 60% Indica", 60),
}