/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentChartBinding
import com.example.gardenbotapp.util.Errors
import com.example.gardenbotapp.util.snack
import com.github.mikephil.charting.data.LineData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ChartFragment : Fragment(R.layout.fragment_chart) {

    private val TAG = "CHART"
    private lateinit var binding: FragmentChartBinding
    private val viewModel: ChartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.populateChartData()
        observeLiveData()
        collectEvents()
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
    private fun observeLiveData() {
        viewModel.measures.observe(viewLifecycleOwner, { list ->
            val chartDataSet = viewModel.prepareDataSetForChart(requireContext(), list)
            val chardata = LineData(chartDataSet)
            binding.chart.apply {
                data = chardata
                invalidate()
            }
        })
        viewModel.measureSub.observe(viewLifecycleOwner, { newMeasure ->
            Log.i(TAG, "onViewCreated: $newMeasure")
            viewModel.refreshChartData(newMeasure)
        })
    }

    private fun showSnack(message: String) {
        binding.root.snack(message)
    }
}