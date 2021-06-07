/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentChartBinding
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.example.gardenbotapp.util.Errors
import com.example.gardenbotapp.util.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ChartFragment : GardenbotBaseFragment<FragmentChartBinding, ChartViewModel>() {

    private val TAG = "CHART"
    override fun getViewBinding() = FragmentChartBinding.inflate(layoutInflater)
    override fun getViewModelClass() = ChartViewModel::class.java

    private fun collectEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.chartEvents.collect { event ->
                when (event) {
                    is Errors.ChartError -> showSnack(event.message)
                    is Errors.HomeError -> showSnack(event.message)
                    is Errors.SubError -> showSnack(event.message)
                    is Errors.TokenError -> showSnack(getString(R.string.network_error_message))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observeLiveData() {
        collectEvents()
        viewModel.measureSub.observe(viewLifecycleOwner, { newMeasure ->
            Log.i(TAG, "onViewCreated: $newMeasure")
            viewModel.refreshChartData(newMeasure)
        })

        viewModel.airHumChartModel.observe(viewLifecycleOwner, {
            binding.chart.aa_drawChartWithChartModel(it)
        })
    }

    private fun showSnack(message: String) {
        binding.root.snack(message)
    }
}