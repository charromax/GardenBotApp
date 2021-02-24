package com.example.gardenbotapp.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentRegisterUserBinding

class RegisterFragment: Fragment(R.layout.fragment_register_user) {
    private lateinit var binding: FragmentRegisterUserBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentRegisterUserBinding.inflate(inflater)

        activity?.title = getString(R.string.register_label)

        return binding.root
    }
}