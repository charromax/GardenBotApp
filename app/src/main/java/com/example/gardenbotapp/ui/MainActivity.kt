/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.databinding.ActivityMainBinding
import com.example.gardenbotapp.ui.home.HomeFragment
import com.example.gardenbotapp.util.getCurrentFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {

    private val preferencesManager = PreferencesManager(this)
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_GardenBotApp)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.app_bar))
        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the action bar for use with the NavController
        setupActionBarWithNavController(navController)

        lifecycleScope.launchWhenStarted {
            preferencesManager.tokenFlow.first()
            preferencesManager.deviceIdFlow.first()
        }
    }

    /**
     * Handle navigation when the user chooses Up from the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (getCurrentFragment() is HomeFragment) {
            finish()
        } else super.onBackPressed()
    }

    companion object {
        const val TAG = "MAIN"
    }

}