/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.onboarding.pages

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentPageTwoBinding
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.example.gardenbotapp.ui.onboarding.OnboardingViewModel
import com.example.gardenbotapp.util.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "ACTIVATE"

@AndroidEntryPoint
class PageTwoFragment : GardenbotBaseFragment<FragmentPageTwoBinding, OnboardingViewModel>() {

    override fun getViewBinding() = FragmentPageTwoBinding.inflate(layoutInflater)
    override fun getViewModelClass() = OnboardingViewModel::class.java
    override var useSharedViewModel = true

    private fun collectFlows() {
        lifecycleScope.launchWhenStarted {
            viewModel.onboardEvents.collect { event ->
                when (event) {
                    is OnboardingViewModel.OnboardingEvents.OnboardingError -> binding.root.snack(
                        event.message
                    )
                    OnboardingViewModel.OnboardingEvents.OnboardingSuccess -> {
                        findNavController().navigate(PageTwoFragmentDirections.actionPageTwoFragmentToEndPageFragment())
                    }
                    is OnboardingViewModel.OnboardingEvents.TokenError -> event.message?.let {
                        binding.root.snack(it)
                    }
                    OnboardingViewModel.OnboardingEvents.DeviceSuscriptionStart -> binding.root.snack(
                        getString(
                            R.string.awaiting_connection
                        )
                    )
                    OnboardingViewModel.OnboardingEvents.EmptyDeviceNameError -> binding.root.snack(
                        getString(R.string.error_empty_device_name)
                    )
                    OnboardingViewModel.OnboardingEvents.DeviceFoundEvent -> binding.root.snack("GardenBot detectado!")
                }
            }
        }
    }

    override fun observeLiveData() {
        collectFlows()
        var devName = ""
        viewModel.deviceName.observe(viewLifecycleOwner, { name ->
            viewModel.subscribeToDevice(name)
            devName = name
        })
        viewModel.subDevice.observe(viewLifecycleOwner, {
            viewModel.activateDevice(devName)
        })

        viewModel.deviceId.observe(viewLifecycleOwner, { deviceID ->
            viewModel.onDeviceActivated(deviceID)
        })
    }


}