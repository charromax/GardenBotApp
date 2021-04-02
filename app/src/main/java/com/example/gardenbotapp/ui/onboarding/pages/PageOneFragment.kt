/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.onboarding.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentPageOneBinding
import com.example.gardenbotapp.ui.onboarding.OnboardingViewModel
import com.example.gardenbotapp.util.snack
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PageOneFragment : Fragment(R.layout.fragment_page_one) {

    private lateinit var binding: FragmentPageOneBinding
    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPageOneBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setTextWatcher()
    }

    private fun setTextWatcher() {
        binding.deviceIdEd.editText?.addTextChangedListener {
            if (it.toString().isNotBlank()) viewModel.setDeviceName(
                it.toString().toUpperCase(Locale.ROOT)
            )
        }
    }

    private fun setOnClickListeners() {
        binding.submitBtn.setOnClickListener {
            findNavController().navigate(PageOneFragmentDirections.actionPageOneFragmentToPageTwoFragment())
            binding.root.snack(getString(R.string.init_activation))
        }
    }
}