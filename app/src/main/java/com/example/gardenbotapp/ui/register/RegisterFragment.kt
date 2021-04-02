/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.register

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentRegisterUserBinding
import com.example.gardenbotapp.ui.login.LoginFragment.Companion.exhaustive
import com.example.gardenbotapp.util.UIState
import com.example.gardenbotapp.util.enable
import com.example.gardenbotapp.util.snack
import com.example.gardenbotapp.util.visible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register_user) {
    private lateinit var binding: FragmentRegisterUserBinding
    private val viewModel: RegisterViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOnClickListeners()
        setTextChangedListeners()
        collectEventsFlow()
        observeLiveData()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun observeLiveData() {
        viewModel.regResponse.observe(viewLifecycleOwner, {
            viewModel.updatePreferences(it.id, it.token)
        })
        viewModel.regResponse.observe(viewLifecycleOwner, {
            setUIState(UIState.READY)
            binding.root.snack(
                "Bienvenidx ${it.username}!",
                Snackbar.LENGTH_SHORT
            )
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToOnboardingActivity())
        })
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
                }.exhaustive
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
        }.exhaustive

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
    private fun setOnClickListeners() {
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterUserBinding.inflate(inflater)
        activity?.title = getString(R.string.register_label)
        setUIState(UIState.READY)
        return binding.root
    }
}