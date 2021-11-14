package com.example.gardenbotapp.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.ui.MainActivity
import com.example.gardenbotapp.ui.MainFragmentDirections
import com.example.gardenbotapp.ui.autopilot_parameters.ParametersFragmentDirections
import com.example.gardenbotapp.ui.home.HomeFragmentDirections
import com.example.gardenbotapp.ui.onboarding.pages.PageOneFragmentDirections
import com.example.gardenbotapp.ui.onboarding.pages.PageTwoFragmentDirections

fun MainActivity.navigateParamsToHome() {
    findNavController(R.id.nav_host_fragment).navigate(ParametersFragmentDirections.actionParametersFragmentToHomeFragment())
}

fun FragmentActivity.navigateMainToLogin() {
    findNavController(R.id.nav_host_fragment).navigate(MainFragmentDirections.actionMainFragmentToLoginFragment())
}

fun FragmentActivity.navigateMainToHome() {
    findNavController(R.id.nav_host_fragment).navigate(MainFragmentDirections.actionMainFragmentToHomeFragment())
}

fun FragmentActivity.navigateHomeToLogin() {
    findNavController(R.id.nav_host_fragment).navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
}

fun FragmentActivity.navigateToOnboarding() {
    findNavController(R.id.nav_host_fragment).navigate(R.id.onboarding)
}

fun FragmentActivity.navigateHomeToParams(){
    findNavController(R.id.nav_host_fragment).navigate(HomeFragmentDirections.actionHomeFragmentToParametersFragment())
}

fun FragmentActivity.navigateOnboardingPageTwo(){
    findNavController(R.id.nav_host_fragment).navigate(PageOneFragmentDirections.actionPageOneFragmentToPageTwoFragment())
}

fun FragmentActivity.navigateOnboardingPageThree(){
    findNavController(R.id.nav_host_fragment).navigate(PageTwoFragmentDirections.actionPageTwoFragmentToEndPageFragment())
}

fun FragmentActivity.navigateHome(){
    findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
}