/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.register

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentRegisterUserBinding
import com.example.gardenbotapp.ui.MainActivity
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.example.gardenbotapp.util.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RegisterFragment : GardenbotBaseFragment<FragmentRegisterUserBinding, RegisterViewModel>() {

    override fun getViewModelClass() = RegisterViewModel::class.java

    override fun getViewBinding() = FragmentRegisterUserBinding.inflate(layoutInflater)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setUpUI() {
        (activity as? MainActivity)?.changeTitle(getString(R.string.register))
        setUIState(UIState.READY)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun observeLiveData() {
        viewModel.regResponse.observe(viewLifecycleOwner, {
            viewModel.updatePreferences(it.id, it.token)
        })
        viewModel.regResponse.observe(viewLifecycleOwner, {
            setUIState(UIState.READY)
            binding.root.snack(
                "Bienvenidx ${it.username}!",
                Snackbar.LENGTH_SHORT
            )
            activity?.navigateToOnboarding()
        })
        collectEventsFlow()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun collectEventsFlow() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.regEvents.collect { event ->
                when (event) {
                    is RegisterViewModel.RegisterEvents.RegisterError -> {
                        setUIState(UIState.READY)
                        binding.root.snack("Error: ${event.message}")
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUIState(state: UIState) {
        when (state) {
            UIState.READY -> {
                binding.progressBar.visible(false)
                with(binding.submitBtn) {
                    enable(false)
                    elevation = 0f
                }
                clearBoxes()
            }
            UIState.TYPING -> {
                binding.progressBar.visible(false)
                with(binding.submitBtn) {
                    enable(
                        viewModel.username.isNotEmpty()
                                && viewModel.password.isNotEmpty()
                                && viewModel.confirmPassword.isNotEmpty()
                                && viewModel.email.isNotEmpty()
                    )
                    elevation = 8f
                }
            }
            UIState.LOADING -> {
                binding.progressBar.visible(true)
                with(binding.submitBtn) {
                    enable(false)
                    elevation = 0f
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setTextChangedListeners() {
        binding.usernameEd.addTextChangedListener {
            viewModel.username = it.toString()
        }

        binding.passwordEd.addTextChangedListener {
            viewModel.password = it.toString()
        }

        binding.repeatPwdEd.addTextChangedListener {
            viewModel.confirmPassword = it.toString()
        }

        binding.emailEd.addTextChangedListener {
            viewModel.email = it.toString()
            setUIState(UIState.TYPING)
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setClickListeners() {
        setTextChangedListeners()
        binding.submitBtn.setOnClickListener {
            setUIState(UIState.LOADING)
            viewModel.registerNewUser()
        }
    }

    private fun clearBoxes() {
        binding.apply {
            usernameEd.setText("")
            passwordEd.setText("")
            repeatPwdEd.setText("")
            emailEd.setText("")
            usernameEd.requestFocus()
        }
    }


}