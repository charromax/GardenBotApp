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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
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
        binding.notRegTextClick.setOnClickListener {
            Log.i(TAG, "onCreateView: Clicked!")
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setOnClickListeners() {
        binding.submitBtn.setOnClickListener {
            viewModel.loginUser()
        }
    }

    private fun setTextChangedListeners() {
        binding.usernameEd.addTextChangedListener {
            viewModel.username = it.toString()
        }
        binding.passwordEd.addTextChangedListener {
            viewModel.password = it.toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setOnClickListeners()
        setTextChangedListeners()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.logEvents.collect { event ->
                when (event) {
                    is LoginViewModel.LoginEvents.LoginError -> {
                        Snackbar.make(
                            binding.root,
                            "Error: ${event.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    LoginViewModel.LoginEvents.LoginSuccess -> {
                        Snackbar.make(
                            binding.root,
                            "Bienvenido a GardenBot!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        viewModel.password = ""
                        viewModel.username = ""
                    }
                }.exhaustive

            }
        }
    }

    companion object {
        const val TAG = "LOGIN"
        val <T> T.exhaustive: T
            get() = this
    }
}