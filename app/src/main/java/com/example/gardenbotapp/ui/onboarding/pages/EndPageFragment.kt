/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.onboarding.pages

import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentEndpageBinding
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.example.gardenbotapp.ui.onboarding.OnboardingViewModel
import com.example.gardenbotapp.util.navigateHome

class EndPageFragment : GardenbotBaseFragment<FragmentEndpageBinding, OnboardingViewModel>() {
    override fun getViewBinding() = FragmentEndpageBinding.inflate(layoutInflater)
    override fun getViewModelClass() = OnboardingViewModel::class.java
    override var useSharedViewModel = true

    override fun setClickListeners() {
        binding.skipButton.setOnClickListener {
            activity?.navigateHome()
        }

        binding.renameButton.setOnClickListener {
            //TODO: send rename mutation
        }
    }

}