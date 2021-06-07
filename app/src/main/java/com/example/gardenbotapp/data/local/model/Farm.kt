/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.local.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

data class Farm(
    val id: String,
    val name: String,
    val createdAt: String,
    val plants: List<Plant>,
    val gardenbotId: String
) {
    val amountOfPlants = plants.size

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateAsDate(): LocalDateTime? {
        return LocalDateTime.parse(createdAt)
    }
}