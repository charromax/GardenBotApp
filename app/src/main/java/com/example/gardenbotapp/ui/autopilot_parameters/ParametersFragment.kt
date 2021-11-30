/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.autopilot_parameters

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import com.example.gardenbotapp.databinding.FragmentAutopilotParamsBinding
import com.example.gardenbotapp.ui.MainActivity
import com.example.gardenbotapp.ui.autopilot_parameters.composables.ParametersScreenMain
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

enum class AutoPilotParams {
    TEMP, SOIL_HUM, AIR_HUM
}

@AndroidEntryPoint
class ParametersFragment :
    GardenbotBaseFragment<FragmentAutopilotParamsBinding, ParametersViewModel>() {

    override fun getViewModelClass() = ParametersViewModel::class.java
    override fun getViewBinding() = FragmentAutopilotParamsBinding.inflate(layoutInflater)

    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.composeFragContent.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val isLoading by viewModel.loadingState.collectAsState()
                MdcTheme {
                    if(isLoading) {
                        //TODO: show loading UI
                    } else ParametersScreenMain(context = requireContext(), paramsViewModel = viewModel)
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}


