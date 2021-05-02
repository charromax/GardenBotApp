/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.deviceorders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentDeviceControlBinding

/**
 * TODO:
 * - integrate MQTT with graphql
 * - build ability to send real time orders to connected devices
 * - use icons for ON/OFF status per device
 * - compact design will allow for greater integrability with UI
 *   further down the line
 */
class OrdersFragment : Fragment(R.layout.fragment_device_control) {
    private lateinit var binding: FragmentDeviceControlBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceControlBinding.inflate(inflater, container, false)
        return binding.root
    }
} 