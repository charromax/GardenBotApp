/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentLoginBinding
import com.example.gardenbotapp.util.enable
import com.example.gardenbotapp.util.snack
import com.example.gardenbotapp.util.visible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        activity?.title = getString(R.string.login)

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setOnClickListeners() {

        binding.submitBtn.setOnClickListener {
            binding.progressBar.visible(true)
            viewModel.loginUser()
        }

        binding.notRegTextClick.setOnClickListener {
            Log.i(TAG, "onCreateView: Clicked!")
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    private fun setTextChangedListeners() {
        binding.usernameEd.addTextChangedListener {
            viewModel.username = it.toString()
        }
        binding.passwordEd.addTextChangedListener {
            viewModel.password = it.toString()
            binding.submitBtn.enable(viewModel.username.isNotEmpty() && it.toString().isNotEmpty())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.progressBar.visible(false)
        binding.submitBtn.enable(false)
        setOnClickListeners()
        setTextChangedListeners()
        observeLiveData()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.logEvents.collect { event ->
                when (event) {
                    is LoginViewModel.LoginEvents.LoginError -> {
                        binding.root.snack("Error: ${event.message}")
                    }
                }.exhaustive

            }
        }
    }

    private fun observeLiveData() {
        viewModel.token.observe(viewLifecycleOwner, { token ->
            binding.progressBar.visible(false)
            if (token.isNotBlank() && token.isNotEmpty()) {
                binding.root.snack(
                    "Bienvenidx a GardenBot!", Snackbar.LENGTH_SHORT
                )
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
            }
        })
        viewModel.logResponse.observe(viewLifecycleOwner, { login ->
            viewModel.updatePreferences(login)
        })
    }

    companion object {
        const val TAG = "LOGIN"
        val <T> T.exhaustive: T
            get() = this
    }
}