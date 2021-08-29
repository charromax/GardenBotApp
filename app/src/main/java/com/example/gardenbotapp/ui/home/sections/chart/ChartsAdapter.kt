/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentChartItemBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.LineData

class ChartsAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val CHART_AIR_HUM = 103
    private val CHART_AIR_TEMP = 104
    private val CHART_SOIL_HUM = 106
    private val chartList = listOf(ChartAirHum(), ChartAirTemp(), ChartSoilHum())
    var airHumLineData: LineData? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var airTempLineData: LineData? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var soilHumLineData: LineData? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getItemViewType(position: Int): Int {
        return when (chartList[position]) {
            is ChartAirHum -> CHART_AIR_HUM
            is ChartAirTemp -> CHART_AIR_TEMP
            is ChartSoilHum -> CHART_SOIL_HUM
            else -> super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_chart_item, parent, false)
        return ChartViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (chartList[position]) {
            is ChartAirHum -> (holder as ChartViewHolder).bind(airHumLineData)
            is ChartAirTemp -> (holder as ChartViewHolder).bind(airTempLineData)
            is ChartSoilHum -> (holder as ChartViewHolder).bind(soilHumLineData)
        }
    }

    override fun getItemCount() = chartList.size

    inner class ChartAirHum
    inner class ChartSoilHum
    inner class ChartAirTemp

    inner class ChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding =
            FragmentChartItemBinding.inflate(LayoutInflater.from(itemView.context))

        fun bind(data: LineData?) {
            data?.let {
                setupChart()
                binding.chart.data = data
                binding.chart.animateX(450, Easing.EaseInElastic)
                binding.chart.notifyDataSetChanged()
            }
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
    }
}