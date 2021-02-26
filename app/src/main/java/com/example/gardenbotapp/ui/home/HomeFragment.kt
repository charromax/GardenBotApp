/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.LineData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        activity?.title = getString(R.string.app_name)

        setHasOptionsMenu(true)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.measures.observe(viewLifecycleOwner, { list ->
            val chartDataSet = viewModel.prepareDataSetForChart(list)
            val chardata = LineData(chartDataSet)
            binding.chart.apply {
                data = chardata
                invalidate()
            }
        })

    }

    companion object {
        const val TAG = "HOMEFRAG"
    }
}