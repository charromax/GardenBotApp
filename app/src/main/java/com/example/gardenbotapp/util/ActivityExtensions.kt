/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

fun AppCompatActivity.getCurrentFragment(): Fragment? {
    val currentNavHost = supportFragmentManager.primaryNavigationFragment as NavHostFragment
    return currentNavHost.childFragmentManager.primaryNavigationFragment
}

fun AppCompatActivity.setAsActionBar(toolbar: Toolbar) {
    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    supportActionBar!!.setDisplayShowHomeEnabled(true)
    toolbar.setNavigationOnClickListener { onBackPressed() }
}