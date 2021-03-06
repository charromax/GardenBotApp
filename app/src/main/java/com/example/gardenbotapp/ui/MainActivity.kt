/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val preferencesManager = PreferencesManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_GardenBotApp)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.app_bar))
    }
    companion object {
        const val TAG = "MAIN"
    }

}