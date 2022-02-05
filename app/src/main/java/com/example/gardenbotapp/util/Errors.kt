/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.util

sealed class Errors {
    data class TokenError(val message: String) : Errors()
    data class HomeError(val message: String) : Errors()
    data class SubError(val message: String) : Errors()
    data class ChartError(val message: String) : Errors()
}


