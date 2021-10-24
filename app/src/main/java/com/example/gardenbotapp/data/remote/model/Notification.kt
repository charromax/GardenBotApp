/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.remote.model

import com.apollographql.apollo.api.Response
import com.example.gardenbotapp.NewNotificationSubscription
import com.example.gardenbotapp.R
import java.util.*

const val DEVICES = 101
const val WEATHER = 102
const val PARAMS_UPDATE = 103
const val WATERING = 105

data class Notification(
    val id: String? = UUID.randomUUID().toString(),
    val createdAt: String? = "",
    val code: Int? = -1,
    val message: String? = "",
) {

    val image = when (code) {
        DEVICES, PARAMS_UPDATE -> R.drawable.ic_internet_of_things
        WEATHER -> R.drawable.ic_weather
        WATERING -> R.drawable.ic_watering_plants
        else -> -1
    }

    companion object {
        fun fromMutation(res: Response<NewNotificationSubscription.Data>): Notification {
            return Notification(
                createdAt = res.data?.newNotification?.createdAt,
                code = res.data?.newNotification?.code,
                message = res.data?.newNotification?.message
            )
        }
    }
}

