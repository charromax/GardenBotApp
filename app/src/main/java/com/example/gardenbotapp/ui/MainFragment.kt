package com.example.gardenbotapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.util.navigateMainToHome
import com.example.gardenbotapp.util.navigateMainToLogin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.checkAutologin()
        mainViewModel.isValidToken.observe(viewLifecycleOwner, { isValidToken ->
            if (isValidToken) activity?.navigateMainToHome() else
                activity?.navigateMainToLogin()
        })
    }
}