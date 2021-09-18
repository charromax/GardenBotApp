/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentChartItemBinding
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
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
    protected abstract fun setChartTitle(binding: FragmentChartItemBinding)
}


class AirHumChartFragment : BaseChartFragment() {
    override fun setValueFormatterForChartType() = PercentageLabelFormatter()
    override fun setChartTitle(binding: FragmentChartItemBinding) {
        binding.chartFragTitle.text = context?.getString(R.string.air_hum_chart_label)
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
    override fun setChartTitle(binding: FragmentChartItemBinding) {
        binding.chartFragTitle.text = context?.getString(R.string.air_temp_chart_label)
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
    override fun setChartTitle(binding: FragmentChartItemBinding) {
        binding.chartFragTitle.text = context?.getString(R.string.soil_hum_chart_label)
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


