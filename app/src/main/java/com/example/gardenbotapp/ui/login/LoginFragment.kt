/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.login

import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.example.gardenbotapp.databinding.FragmentLoginBinding
import com.example.gardenbotapp.util.UIState
import com.example.gardenbotapp.util.enable
import com.example.gardenbotapp.util.snack
import com.example.gardenbotapp.util.visible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        requireActivity().app_bar.title = getString(R.string.login)
        binding.progressBar.visible(false)
        binding.submitBtn.enable(false)
        binding.submitBtn.elevation = 0f
        setHasOptionsMenu(true)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setOnClickListeners() {

        binding.submitBtn.setOnClickListener {
            setUIState(UIState.LOADING)
            viewModel.loginUser()
        }

        binding.notRegTextClick.setOnClickListener {
            Log.i(TAG, "onCreateView: Clicked!")
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
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
                with(binding.submitBtn) {
                    enable(
                        viewModel.username.isNotEmpty()
                                && viewModel.password.isNotEmpty()
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
            setUIState(UIState.TYPING)

        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOnClickListeners()
        setTextChangedListeners()
        observeLiveData()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.logEvents.collect { event ->
                when (event) {
                    is LoginViewModel.LoginEvents.LoginError -> {
                        setUIState(UIState.READY)
                        binding.root.snack("Error: ${event.message}")
                    }
                }

            }
        }
    }

    private fun clearBoxes() {
        with(binding) {
            usernameEd.setText("")
            passwordEd.setText("")
            usernameEd.requestFocus()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun observeLiveData() {

        viewModel.token.observe(viewLifecycleOwner, { token ->
            binding.progressBar.visible(false)
            if (token.isNotBlank() && token.isNotEmpty()) {
                setUIState(UIState.READY)
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