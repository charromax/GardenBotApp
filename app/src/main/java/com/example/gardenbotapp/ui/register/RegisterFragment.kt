/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentRegisterUserBinding
import com.example.gardenbotapp.ui.login.LoginFragment.Companion.exhaustive
import com.example.gardenbotapp.util.snack
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RegisterFragment: Fragment(R.layout.fragment_register_user) {
    private lateinit var binding: FragmentRegisterUserBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOnClickListeners()
        setTextChangedListeners()
        collectEventsFlow()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.regResponse.observe(viewLifecycleOwner, {
            viewModel.updatePreferences(it.id, it.token)
        })
        viewModel.regResponse.observe(viewLifecycleOwner, {
            clearBoxes()
            binding.root.snack(
                "Bienvenidx ${it.username}!",
                Snackbar.LENGTH_SHORT
            )
            findNavController().navigateUp()
        })
    }

    private fun collectEventsFlow() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.regEvents.collect { event ->
                when (event) {
                    is RegisterViewModel.RegisterEvents.RegisterError -> {
                        binding.root.snack("Error: ${event.message}")
                    }
                }.exhaustive
            }
        }
    }

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
        }

    }

    private fun setOnClickListeners() {
        binding.submitBtn.setOnClickListener {
            viewModel.registerNewUser()
        }
    }

    private fun clearBoxes() {
        binding.apply {
            usernameEd.setText("")
            passwordEd.setText("")
            repeatPwdEd.setText("")
            emailEd.setText("")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterUserBinding.inflate(inflater)
        activity?.title = getString(R.string.register_label)
        return binding.root
    }
}