/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gardenbotapp.R
import com.example.gardenbotapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_GardenBotApp)

        setContentView(binding.root)
//        observeViewModel()
        setSupportActionBar(findViewById(R.id.app_bar))
    }

//    private fun observeViewModel() {
//
//        viewModel.allMeasures.observe(this, Observer {
//            Log.i(TAG, "onCreate: $it")
//        })
//
//        viewModel.regResponse.observe(this, Observer { res ->
//            with(res) {
//                this?.let {
//                    val user = User(
//                        id,
//                        username,
//                        token,
//                        createdAt
//                    )
//                    Log.i(TAG, "User Created: $user")
//                }
//            }
//        })
//
//        viewModel.logResponse.observe(this, Observer { res ->
//            with(res) {
//                this?.let {
//                    val devices = ArrayList<Device>()
//                    it.devices.map { dev ->
//                        dev?.let {
//                            devices.add(
//                                Device(
//                                    dev.id,
//                                    dev.deviceName,
//                                    dev.createdAt
//                                )
//                            )
//                        }
//                    }
//                    val user = User(
//                        id,
//                        username,
//                        token,
//                        createdAt,
//                        devices
//                    )
//                    Log.i(TAG, "User Logged In: $user")
//                }
//            }
//        })
//    }

    companion object {
        const val TAG = "MAIN"
    }

}