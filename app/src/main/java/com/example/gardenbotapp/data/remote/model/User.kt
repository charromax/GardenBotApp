/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.remote.model

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class User(
    val id:String,
    val username: String,
    val token: String,
    val createdAt: String,
    val devices: ArrayList<Device> = arrayListOf()

): Parcelable {
    @IgnoredOnParcel
    val deviceCount: Int = devices.size

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateAsDate(): LocalDateTime? {
        return LocalDateTime.parse(createdAt)
    }

}