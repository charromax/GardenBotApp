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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentHomeBinding
import com.example.gardenbotapp.util.snack
import com.github.mikephil.charting.data.LineData
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


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
        lifecycleScope.launchWhenStarted {
            viewModel.homeEvents.collect { event ->
                when (event) {
                    is HomeViewModel.HomeEvents.TokenError -> {
                        binding.root.snack(event.message!!, Snackbar.LENGTH_SHORT)
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                    }
                    is HomeViewModel.HomeEvents.HomeError -> binding.root.snack(event.message)
                }

            }
        }

        viewModel.measures.observe(viewLifecycleOwner, { list ->
            val chartDataSet = viewModel.prepareDataSetForChart(list)
            val chardata = LineData(chartDataSet)
            binding.chart.apply {
                data = chardata
                invalidate()
            }
            TODO("modify chart list building logic to make it compatible with measure subscription")
        })
        viewModel.measureSub.observe(viewLifecycleOwner, {
            TODO("add newmeasure to list and refresh chart")
        })

    }

    companion object {
        const val TAG = "HOMEFRAG"
    }
}