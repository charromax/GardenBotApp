/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.onboarding.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentPageTwoBinding
import com.example.gardenbotapp.ui.onboarding.OnboardingViewModel
import com.example.gardenbotapp.util.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "ACTIVATE"

@AndroidEntryPoint
class PageTwoFragment : Fragment(R.layout.fragment_page_two) {

    private val viewModel: OnboardingViewModel by activityViewModels()
    private lateinit var binding: FragmentPageTwoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPageTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeLiveData()
        collectFlows()
    }

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

    private fun observeLiveData() {
        var devName = ""
        viewModel.deviceName.observe(viewLifecycleOwner, { name ->
            viewModel.subscribeToDevice(name)
            devName = name
        })
        viewModel.subDevice.observe(viewLifecycleOwner, { device ->
            viewModel.activateDevice(devName)
        })

        viewModel.deviceId.observe(viewLifecycleOwner, { deviceID ->
            viewModel.onDeviceActivated(deviceID)
        })
    }


}