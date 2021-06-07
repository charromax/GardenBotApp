/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.remote.model.Notification
import com.example.gardenbotapp.databinding.ActivityMainBinding
import com.example.gardenbotapp.ui.home.HomeFragment
import com.example.gardenbotapp.util.getCurrentFragment
import com.example.gardenbotapp.util.setAsActionBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {

    private val preferencesManager = PreferencesManager(this)
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

        //retrieve default device's id and token to use in this session
        // this comes from a stackoverflow where someone said if you do this in MainActivity
        // then you can use runBlocking{} to get the data when you need it
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

    fun changeTitle(title: String) {
        supportActionBar?.title = title
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