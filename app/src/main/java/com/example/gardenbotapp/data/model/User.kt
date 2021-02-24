package com.example.gardenbotapp.data.model

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.time.LocalDateTime
import kotlin.collections.ArrayList

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