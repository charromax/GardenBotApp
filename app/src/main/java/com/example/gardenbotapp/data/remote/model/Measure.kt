/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.remote.model

import com.apollographql.apollo.api.Response
import com.example.gardenbotapp.NewMeasureSubscription

data class Measure(
    val id: String,
    val createdAt: String,
    val airHum: Double,
    val airTemp: Double,
    val soilHum: Double
) {
    companion object {
        fun fromResponse(res: Response<NewMeasureSubscription.Data>): Measure {
            return Measure(
                id = res.data?.newMeasure?.id ?: "",
                airTemp = res.data?.newMeasure?.airTemp ?: 0.0,
                airHum = res.data?.newMeasure?.airHum ?: 0.0,
                soilHum = res.data?.newMeasure?.soilHum ?: 0.0,
                createdAt = res.data?.newMeasure?.createdAt ?: ""
            )
        }
    }
}