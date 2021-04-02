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
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentEndpageBinding
import com.example.gardenbotapp.ui.onboarding.OnboardingViewModel

class EndPageFragment : Fragment(R.layout.fragment_endpage) {
    private lateinit var binding: FragmentEndpageBinding
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEndpageBinding.inflate(inflater)

        return binding.root
    }

}