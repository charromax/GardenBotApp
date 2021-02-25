/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    val snack = Snackbar.make(this, message, duration)
    snack.show()
}