/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home

import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentHomeBinding
import com.example.gardenbotapp.ui.MainActivity
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.example.gardenbotapp.util.Errors
import com.example.gardenbotapp.util.snack
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "HOME"

@AndroidEntryPoint
class HomeFragment : GardenbotBaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override fun getViewBinding() = FragmentHomeBinding.inflate(layoutInflater)
    override fun getViewModelClass() = HomeViewModel::class.java

    override fun setUpUI() {
        (activity as MainActivity).changeTitle(getString(R.string.gardenhome))
        setHasOptionsMenu(true)
    }

    override fun observeLiveData() {

        viewModel.deviceId.observe(viewLifecycleOwner, { deviceID ->
            if (deviceID.isEmpty()) {
//                findNavController().navigate(R.id.onboarding)
            }
        })

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