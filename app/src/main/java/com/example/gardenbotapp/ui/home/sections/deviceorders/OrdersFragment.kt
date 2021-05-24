/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.deviceorders

import androidx.lifecycle.lifecycleScope
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentDeviceControlBinding
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.example.gardenbotapp.util.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * MQTT integration with graphql for real-time device control, using livedata for state update
 * and flow for UI messaging
 */
const val BOTTOM = 0f
const val TOP = 2f

@AndroidEntryPoint
class OrdersFragment : GardenbotBaseFragment<FragmentDeviceControlBinding, OrdersViewModel>() {
    override fun getViewBinding() = FragmentDeviceControlBinding.inflate(layoutInflater)
    override fun getViewModelClass() = OrdersViewModel::class.java

    private fun collectEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.ordersEvents.collect { event ->
                when (event) {
                    OrdersViewModel.OrdersEvents.OnInitialState -> viewModel.refreshDeviceState()
                    is OrdersViewModel.OrdersEvents.OnOrderError -> showSnack(event.message)
                    OrdersViewModel.OrdersEvents.OnTokenError -> showSnack(getString(R.string.network_error_message))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setInitialState()
    }

    override fun observeLiveData() {
        with(viewModel) {
            //observe changes in device state and update buttons
            onDeviceStateStateChanged.observe(viewLifecycleOwner, { incomingChange ->
                when (incomingChange.device) {
                    ConnectedDevice.LAMPARA -> binding.lampara.isChecked = incomingChange.newState
                    ConnectedDevice.VENTILADOR -> binding.ventilator.isChecked =
                        incomingChange.newState
                    ConnectedDevice.EXTRACTOR -> binding.extractor.isChecked =
                        incomingChange.newState
                    ConnectedDevice.INTRACTOR -> binding.intractor.isChecked =
                        incomingChange.newState
                }

            })
        }
        collectEvents()
    }

    override fun setClickListeners() {
        with(binding) {
            lampara.setOnCheckedChangeListener { _, state ->
                updateState(state, ConnectedDevice.LAMPARA)
            }
            ventilator.setOnCheckedChangeListener { _, state ->
                updateState(state, ConnectedDevice.VENTILADOR)
            }
            extractor.setOnCheckedChangeListener { _, state ->
                updateState(state, ConnectedDevice.EXTRACTOR)
            }
            intractor.setOnCheckedChangeListener { _, state ->
                updateState(state, ConnectedDevice.INTRACTOR)
            }
        }
    }

    private fun updateState(state: Boolean, device: ConnectedDevice) {
        when (device) {
            ConnectedDevice.LAMPARA -> binding.lampContainer.cardElevation =
                if (state) BOTTOM else TOP
            ConnectedDevice.VENTILADOR -> binding.ventContainer.cardElevation =
                if (state) BOTTOM else TOP
            ConnectedDevice.EXTRACTOR -> binding.extContainer.cardElevation =
                if (state) BOTTOM else TOP
            ConnectedDevice.INTRACTOR -> binding.intContainer.cardElevation =
                if (state) BOTTOM else TOP
        }
        viewModel.sendOrder(device, state)
    }

    private fun showSnack(message: String) {
        binding.root.snack(message)
    }
} 