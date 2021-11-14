 /*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.onboarding.pages

 import androidx.core.widget.addTextChangedListener
 import androidx.navigation.fragment.findNavController
 import com.example.gardenbotapp.R
 import com.example.gardenbotapp.databinding.FragmentPageOneBinding
 import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
 import com.example.gardenbotapp.ui.onboarding.OnboardingViewModel
 import com.example.gardenbotapp.util.navigateOnboardingPageTwo
 import com.example.gardenbotapp.util.snack
 import dagger.hilt.android.AndroidEntryPoint
 import java.util.*

 @AndroidEntryPoint
 class PageOneFragment : GardenbotBaseFragment<FragmentPageOneBinding, OnboardingViewModel>() {

     override fun getViewBinding() = FragmentPageOneBinding.inflate(layoutInflater)
     override fun getViewModelClass() = OnboardingViewModel::class.java
     override var useSharedViewModel = true

     private fun setTextWatcher() {
         binding.deviceIdEd.editText?.addTextChangedListener {
             if (it.toString().isNotBlank()) viewModel.setDeviceName(
                 it.toString().toUpperCase(Locale.ROOT)
             )
         }
     }

     override fun setClickListeners() {
         setTextWatcher()
         binding.submitBtn.setOnClickListener {
             activity?.navigateOnboardingPageTwo()
             binding.root.snack(getString(R.string.init_activation))
         }
     }
 }