/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.onboarding.activatepages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentPage2Binding
import com.example.gardenbotapp.ui.onboarding.OnboardingViewModel
import com.example.gardenbotapp.util.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PageTwoFragment : Fragment(R.layout.fragment_page2) {

    private val viewModel: OnboardingViewModel by viewModels()
    private lateinit var binding: FragmentPage2Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPage2Binding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOnClickListeners()
        observeLiveData()
        collectEvents()
    }

    private fun collectEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.onboardEvents.collect { event ->
                when (event) {
                    is OnboardingViewModel.OnboardingEvents.OnboardingError -> binding.root.snack(
                        event.message
                    )
                    OnboardingViewModel.OnboardingEvents.OnboardingSuccess -> {
                        TODO("show success snack and advance to final fragment then home")
                    }
                    is OnboardingViewModel.OnboardingEvents.TokenError -> event.message?.let {
                        binding.root.snack(it)
                    }
                }
            }
        }
    }

    private fun observeLiveData() {
        viewModel.deviceId.observe(viewLifecycleOwner, Observer { deviceId ->
            if (deviceId.isNotEmpty()) {
                viewModel.onDeviceActivated(deviceId)
            }
        })
    }

    private fun setOnClickListeners() {
        binding.submitBtn.setOnClickListener {
            viewModel.activateDevice()
        }
    }
}