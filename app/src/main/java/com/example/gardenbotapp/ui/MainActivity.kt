package com.example.gardenbotapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.model.Device
import com.example.gardenbotapp.data.model.User
import com.example.gardenbotapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.sql.Date

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

    @ExperimentalCoroutinesApi
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