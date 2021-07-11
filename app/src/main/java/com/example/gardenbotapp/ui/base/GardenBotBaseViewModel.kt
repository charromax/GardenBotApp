/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.base

import androidx.lifecycle.ViewModel
import com.example.gardenbotapp.RefreshTokenQuery
import com.example.gardenbotapp.data.domain.GardenBotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class GardenBotBaseViewModel @Inject constructor() : ViewModel(), GardenBotRepository {

    @Inject
    lateinit var gardenBotRepository: GardenBotRepository

    override suspend fun refreshToken(token: String): RefreshTokenQuery.Data? {
        return gardenBotRepository.refreshToken(token)
    }
}