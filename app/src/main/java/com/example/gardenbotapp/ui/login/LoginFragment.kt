/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.login

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentLoginBinding
import com.example.gardenbotapp.ui.MainActivity
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.example.gardenbotapp.util.UIState
import com.example.gardenbotapp.util.enable
import com.example.gardenbotapp.util.snack
import com.example.gardenbotapp.util.visible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class LoginFragment : GardenbotBaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override fun getViewModelClass() = LoginViewModel::class.java

    override fun getViewBinding() = FragmentLoginBinding.inflate(layoutInflater)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setClickListeners() {
        setTextChangedListeners()
        binding.submitBtn.setOnClickListener {
            setUIState(UIState.LOADING)
            when {
                binding.usernameEd.editText?.text?.isEmpty() == true -> binding.usernameEd.error =
                    getString(R.string.dont_forget_me)
                binding.passwordEd.editText?.text?.isEmpty() == true -> binding.passwordEd.error =
                    getString(
                        R.string.dont_forget_me
                    )
                else -> viewModel.loginUser()
            }
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
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setTextChangedListeners() {
        binding.usernameEd.editText?.addTextChangedListener {
            viewModel.username = it.toString()
        }
        binding.passwordEd.editText?.addTextChangedListener {
            viewModel.password = it.toString()
            setUIState(UIState.TYPING)

        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setUpUI() {
        (activity as MainActivity).changeTitle(getString(R.string.login))
        binding.progressBar.visible(false)
        binding.submitBtn.enable(false)
        binding.submitBtn.elevation = 0f
        setHasOptionsMenu(true)

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
            usernameEd.editText?.setText("")
            passwordEd.editText?.setText("")
            usernameEd.editText?.requestFocus()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun observeLiveData() {

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
    }

}