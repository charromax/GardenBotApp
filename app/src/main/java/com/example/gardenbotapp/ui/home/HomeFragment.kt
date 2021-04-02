/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentHomeBinding
import com.example.gardenbotapp.util.Errors
import com.example.gardenbotapp.util.snack
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "HOME"
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        activity?.title = getString(R.string.app_name)
        observeLiveData()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun observeLiveData() {
        viewModel.deviceId.observe(viewLifecycleOwner, { deviceID ->
            if (deviceID.isEmpty()) {
                findNavController().navigate(R.id.onboarding)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenStarted {
            viewModel.homeEvents.collect { event ->
                when (event) {
                    is Errors.TokenError -> {
                        binding.root.snack(event.message!!, Snackbar.LENGTH_SHORT)
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                    }
                    is Errors.HomeError -> binding.root.snack(event.message)
                    else -> Log.i(TAG, "onViewCreated: $event")
                }
            }
        }

    }

}