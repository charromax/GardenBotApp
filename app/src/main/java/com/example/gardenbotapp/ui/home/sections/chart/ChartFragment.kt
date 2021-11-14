/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentChartBinding
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import android.content.Intent
import com.example.gardenbotapp.util.*
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ChartFragment : GardenbotBaseFragment<FragmentChartBinding, ChartViewModel>() {

    private var hasUpdatedYet = false
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
        setupChart()
        collectEvents()
    }

    override fun setClickListeners() {
        super.setClickListeners()
        binding.shareButton.setOnClickListener {
            viewModel.takeAndSaveScreenShot(this)
        }
    }

    override fun onResume() {
        super.onResume()
        setupAirTempLiveViewFormatter()
    }

    private fun setupAirTempLiveViewFormatter() {
        binding.airTempLiveView.setTextFormatter {
            val temp = (it * MAX_ALLOWED_TEMPERATURE) / 100
            temp.toString().toTemperatureString()
        }
    }

    /**
     * setup chart UI
     */
    private fun setupChart() {
        chartAdapter = ChartsAdapter(charts, this)
        binding.pager.adapter = chartAdapter
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        })
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
            if (hasUpdatedYet.not()) {
                viewModel.manualUpdateNewMeasure(it.last())
                hasUpdatedYet = true
            }
        })

        viewModel.liveAirHumData.observe(viewLifecycleOwner, {
            lifecycleScope.launchWhenResumed { binding.airHumLiveView.setProgress(it, true) }
        })
        viewModel.liveAirTempData.observe(viewLifecycleOwner, {
            lifecycleScope.launchWhenResumed { binding.airTempLiveView.setProgress(it, true) }
        })
        viewModel.liveSoilHumData.observe(viewLifecycleOwner, {
            lifecycleScope.launchWhenResumed { binding.soilHumLiveView.setProgress(it, true) }
        })

        viewModel.liveAirHumData.observe(viewLifecycleOwner, {
            binding.airHumLiveView.setProgress(it, true)
        })
        viewModel.liveAirTempData.observe(viewLifecycleOwner, {
            binding.airTempLiveView.setProgress(it, true)
        })
        viewModel.liveSoilHumData.observe(viewLifecycleOwner, {
            binding.soilHumLiveView.setProgress(it, true)
        })

        viewModel.chartScreenShot.observe(viewLifecycleOwner) {
            activity?.shareImage(it)
        }
    }

    private fun showSnack(message: String) {
        binding.root.snack(message)
    }
}