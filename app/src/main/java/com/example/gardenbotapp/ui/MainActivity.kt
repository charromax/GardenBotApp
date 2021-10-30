/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.model.Notification
import com.example.gardenbotapp.databinding.ActivityMainBinding
import com.example.gardenbotapp.ui.autopilot_parameters.ParametersFragment
import com.example.gardenbotapp.ui.autopilot_parameters.ParametersFragmentDirections
import com.example.gardenbotapp.ui.home.HomeFragment
import com.example.gardenbotapp.util.getCurrentFragment
import com.example.gardenbotapp.util.setAsActionBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    val notificationsList = arrayListOf<Notification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_GardenBotApp)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAsActionBar(binding.toolbar)
        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    /**
     * Handle navigation when the user chooses Up from the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun changeTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onBackPressed() {
        when {
            getCurrentFragment() is HomeFragment -> {
                finish()
            }
            getCurrentFragment() is ParametersFragment -> {
                navController.navigate(ParametersFragmentDirections.actionParametersFragmentToHomeFragment())
            }
            else -> super.onBackPressed()
        }
    }

    companion object {
        const val TAG = "MAIN"
    }

}