/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.autopilot_parameters

import com.example.gardenbotapp.di.ApplicationIoScope
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel @Inject constructor() : GardenBotBaseViewModel() {
    @Inject
    @ApplicationIoScope
    lateinit var uiScope: CoroutineScope

}