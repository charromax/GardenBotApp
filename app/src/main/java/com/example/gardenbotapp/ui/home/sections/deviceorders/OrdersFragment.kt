/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.deviceorders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentDeviceControlBinding
import com.example.gardenbotapp.util.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * MQTT integration with graphql for real-time device control, using livedata for state update
 * and flow for UI messaging
 */
@AndroidEntryPoint
class OrdersFragment : Fragment(R.layout.fragment_device_control) {
    private lateinit var binding: FragmentDeviceControlBinding
    private val viewModel: OrdersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setInitialState()
        setTogglesListeners()
        observeLiveData()
        collectEvents()
    }

    private fun collectEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.ordersEvents.collect { event ->
                when (event) {
                    OrdersViewModel.OrdersEvents.OnInitialState -> viewModel.refreshDeviceState()
                    is OrdersViewModel.OrdersEvents.OnOrderError -> showSnack(event.message)
                    is OrdersViewModel.OrdersEvents.OnOrderSent -> showSnack(event.message)
                    OrdersViewModel.OrdersEvents.OnTokenError -> showSnack(getString(R.string.network_error_message))
                }
            }
        }
    }

    private fun observeLiveData() {
        with(viewModel) {
            //observe changes in device state and update buttons
            onDeviceStateStateChanged.observe(viewLifecycleOwner, { incomingChange ->
                when (incomingChange.device) {
                    ConnectedDevice.LAMPARA -> binding.lamp.isChecked = incomingChange.newState
                    ConnectedDevice.VENTILADOR -> binding.ventilator.isChecked =
                        incomingChange.newState
                    ConnectedDevice.EXTRACTOR -> binding.extractor.isChecked =
                        incomingChange.newState
                    ConnectedDevice.INTRACTOR -> binding.intractor.isChecked =
                        incomingChange.newState
                }

            })
        }
    }

    private fun setTogglesListeners() {
        with(binding) {
            lamp.setOnCheckedChangeListener { _, state ->
                viewModel.sendOrder(ConnectedDevice.LAMPARA, state)
            }
            ventilator.setOnCheckedChangeListener { _, state ->
                viewModel.sendOrder(ConnectedDevice.VENTILADOR, state)
            }
            extractor.setOnCheckedChangeListener { _, state ->
                viewModel.sendOrder(ConnectedDevice.EXTRACTOR, state)
            }
            intractor.setOnCheckedChangeListener { _, state ->
                viewModel.sendOrder(ConnectedDevice.INTRACTOR, state)
            }
        }
    }

    private fun showSnack(message: String) {
        binding.root.snack(message)
    }
} 