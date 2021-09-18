/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
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
    override var useSharedViewModel = true
    private val calculationsViewModel: ChartCalculationsViewModel by activityViewModels()
    private lateinit var chartAdapter: ChartsAdapter
    private val charts =
        listOf(AirHumChartFragment(), AirTempChartFragment(), SoilHumChartFragment())

    override fun setUpUI() {
        super.setUpUI()
        collectEvents()
        setupChart()
    }

    /**
     * setup chart UI
     */
    private fun setupChart() {
        chartAdapter = ChartsAdapter(charts, this)
        binding.pager.adapter = chartAdapter
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
            viewModel.populateChartData()
        }
        viewModel.measureSub.observe(viewLifecycleOwner, { newMeasure ->
            Log.i(TAG, "onViewCreated: $newMeasure")
            viewModel.refreshChartData(newMeasure)
        })

        viewModel.measures.observe(viewLifecycleOwner, {
            calculationsViewModel.initModelCalculations(it)
        })
    }

    private fun showSnack(message: String) {
        binding.root.snack(message)
    }
}