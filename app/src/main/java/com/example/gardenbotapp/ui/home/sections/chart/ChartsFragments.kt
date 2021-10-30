/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.content.pm.ActivityInfo
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentChartItemBinding
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.example.gardenbotapp.ui.home.HomeFragmentDirections
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.formatter.ValueFormatter

abstract class BaseChartFragment :
    GardenbotBaseFragment<FragmentChartItemBinding, ChartViewModel>() {
    override fun getViewModelClass() = ChartViewModel::class.java
    override var useSharedViewModel = true
    override fun getViewBinding() = FragmentChartItemBinding.inflate(LayoutInflater.from(context))
    protected val calculationsViewModel: ChartCalculationsViewModel by activityViewModels()

    override fun setUpUI() {
        super.setUpUI()
        setupChart()
    }

    /**
     * setup chart UI
     */
    private fun setupChart() {
        with(binding.chart) {
            description = Description().apply { isEnabled = false }
            setNoDataText(context.getString(R.string.chart_no_data_message))
            axisRight.valueFormatter = setValueFormatterForChartType()
            axisLeft.isEnabled = false
            xAxis.isEnabled = false
            invalidate()
        }
    }

    protected abstract fun setValueFormatterForChartType(): ValueFormatter?
}

class AirHumChartFragment : BaseChartFragment() {
    override fun setValueFormatterForChartType() = PercentageLabelFormatter()

    override fun setClickListeners() {
        super.setClickListeners()
        binding.chart.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFullScreenAirHumFragment())
        }
    }
    override fun observeLiveData() {
        super.observeLiveData()
        calculationsViewModel.airHumLineData.observe(viewLifecycleOwner, {
            binding.chart.data = it
            with(binding.chart) {
                animateX(450, Easing.EaseInElastic)
                notifyDataSetChanged()
                invalidate()
            }
        })
    }
}

class AirTempChartFragment : BaseChartFragment() {
    override fun setValueFormatterForChartType() = TemperatureLabelFormatter()

    override fun setClickListeners() {
        super.setClickListeners()
        binding.chart.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFullScreenAirTempFragment())
        }
    }
    override fun observeLiveData() {
        super.observeLiveData()
        calculationsViewModel.airTempLineData.observe(viewLifecycleOwner, {
            binding.chart.data = it
            with(binding.chart) {
                animateX(450, Easing.EaseInElastic)
                notifyDataSetChanged()
                invalidate()
            }
        })
    }
}

class SoilHumChartFragment : BaseChartFragment() {
    override fun setValueFormatterForChartType() = PercentageLabelFormatter()
    override fun setClickListeners() {
        super.setClickListeners()
        binding.chart.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFullScreenSoilHumFragment())
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()
        calculationsViewModel.soilHumLineData.observe(viewLifecycleOwner, {
            binding.chart.data = it
            with(binding.chart) {
                animateX(450, Easing.EaseInElastic)
                notifyDataSetChanged()
                invalidate()
            }
        })
    }
}

class FullScreenAirHumFragment : BaseChartFragment() {
    override fun setUpUI() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.setUpUI()
    }
    override fun setValueFormatterForChartType() = PercentageLabelFormatter()
    override fun observeLiveData() {
        super.observeLiveData()
        calculationsViewModel.fullAirHumLineData.observe(viewLifecycleOwner, {
            binding.chart.data = it
            with(binding.chart) {
                animateX(450, Easing.EaseInElastic)
                notifyDataSetChanged()
                invalidate()
            }
        })
    }

    override fun onDestroyView() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        super.onDestroyView()
    }
}

class FullScreenSoilHumFragment : BaseChartFragment() {
    override fun setUpUI() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.setUpUI()
    }
    override fun setValueFormatterForChartType() = PercentageLabelFormatter()
    override fun observeLiveData() {
        super.observeLiveData()
        calculationsViewModel.fullSoilHumLineData.observe(viewLifecycleOwner, {
            binding.chart.data = it
            with(binding.chart) {
                animateX(450, Easing.EaseInElastic)
                notifyDataSetChanged()
                invalidate()
            }
        })
    }

    override fun onDestroyView() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        super.onDestroyView()
    }
}

class FullScreenAirTempFragment : BaseChartFragment() {
    override fun setUpUI() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.setUpUI()
    }
    override fun setValueFormatterForChartType() = TemperatureLabelFormatter()
    override fun observeLiveData() {
        super.observeLiveData()
        calculationsViewModel.fullAirTempLineData.observe(viewLifecycleOwner, {
            binding.chart.data = it
            with(binding.chart) {
                animateX(450, Easing.EaseInElastic)
                notifyDataSetChanged()
                invalidate()
            }
        })
    }

    override fun onDestroyView() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        super.onDestroyView()
    }
}

