/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.base

import androidx.lifecycle.ViewModel
import com.example.gardenbotapp.RefreshTokenQuery
import com.example.gardenbotapp.data.remote.GardenBotContract
import com.example.gardenbotapp.data.remote.GardenBotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class GardenBotBaseViewModel @Inject constructor(
    private val gardenBotRepository: GardenBotRepository
) : ViewModel(), GardenBotContract {

    override suspend fun refreshToken(token: String): RefreshTokenQuery.Data? {
        TODO("Not yet implemented")
    }
}