/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentChartBinding
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.example.gardenbotapp.util.Errors
import com.example.gardenbotapp.util.snack
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ChartFragment : GardenbotBaseFragment<FragmentChartBinding, ChartViewModel>() {

    private val TAG = "CHART"
    override fun getViewBinding() = FragmentChartBinding.inflate(layoutInflater)
    override fun getViewModelClass() = ChartViewModel::class.java
    private val calculationsViewModel: ChartCalculationsViewModel by viewModels()

    override fun setUpUI() {
        super.setUpUI()
        collectEvents()
        setupChart()
    }

    /**
     * setup chart UI
     */
    private fun setupChart() {
        with(binding.chart) {
            description = Description().apply { isEnabled = false }
            setNoDataText(context.getString(R.string.chart_no_data_message))
            axisRight.valueFormatter = TemperatureLabelFormatter()
            axisLeft.isEnabled = false
            xAxis.isEnabled = false
            invalidate()
        }
    }

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
        //launch on started and repeat
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.populateChartData()
            }
        }
        viewModel.measureSub.observe(viewLifecycleOwner, { newMeasure ->
            Log.i(TAG, "onViewCreated: $newMeasure")
            viewModel.refreshChartData(newMeasure)
        })

        viewModel.measures.observe(viewLifecycleOwner, {
            calculationsViewModel.initModelCalculations(it)
        })

        calculationsViewModel.airTempLineData.observe(viewLifecycleOwner, {
            binding.chart.data = it
            binding.chart.animateY(700, Easing.EaseInOutCubic)
            binding.chart.notifyDataSetChanged()
        })
    }

    private fun showSnack(message: String) {
        binding.root.snack(message)
    }
}