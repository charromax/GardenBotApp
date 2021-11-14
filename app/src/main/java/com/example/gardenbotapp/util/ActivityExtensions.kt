/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.util

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.gardenbotapp.R
import com.example.gardenbotapp.ui.MainActivity

import android.content.pm.PackageManager




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

fun FragmentActivity.shareImage(image: Uri) {
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, image)
        type = "image/png"
    }
    val chooser = Intent.createChooser(shareIntent, resources.getText(R.string.send_to))
    val resInfoList =
        this.packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

    for (resolveInfo in resInfoList) {
        val packageName = resolveInfo.activityInfo.packageName
        grantUriPermission(
            packageName,
            image,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
    Log.i(MainActivity.TAG, "shareImage: $image")
    startActivity(chooser)
}