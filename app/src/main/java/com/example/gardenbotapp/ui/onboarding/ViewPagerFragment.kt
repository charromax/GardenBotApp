/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.FragmentViewPagerBinding
import com.example.gardenbotapp.ui.onboarding.activatepages.EndPageFragment
import com.example.gardenbotapp.ui.onboarding.activatepages.PageOneFragment
import com.example.gardenbotapp.ui.onboarding.activatepages.PageTwoFragment

class ViewPagerFragment : Fragment(R.layout.fragment_view_pager) {
    private lateinit var binding: FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPagerBinding.inflate(inflater)
        val fragmentList =
            arrayListOf<Fragment>(PageOneFragment(), PageTwoFragment(), EndPageFragment())
        val adapter =
            ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle, fragmentList)
        binding.pager.adapter = adapter

        return binding.root
    }
}