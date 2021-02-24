package com.example.gardenbotapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Parcelize
data class Device(
    val id:String,
    val serial: String,
    val createdAt: String
): Parcelable