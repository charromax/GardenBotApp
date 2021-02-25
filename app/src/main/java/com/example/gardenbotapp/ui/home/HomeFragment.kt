/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.remote.Client
import com.example.gardenbotapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        activity?.title = getString(R.string.app_name)

        val chart = AnyChart.line()
        GlobalScope.launch {
            val measures = Client.getAllMeasures("601599a09606f008af118b79")
            measures.collect { measures ->

            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }
}