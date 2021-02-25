/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.LoginUserMutation
import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.data.GardenBotRepository
import com.example.gardenbotapp.type.RegisterInput
import com.example.gardenbotapp.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MeasuresViewModel @Inject constructor(
    private val gardenBotRepository: GardenBotRepository,
    preferencesManager: PreferencesManager,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {

    private val prefs = preferencesManager.preferencesFlow

    private val measuresFlow = prefs.flatMapLatest { gardenBotRepository.getMeasuresForDevice(it.deviceId) }
    val allMeasures = measuresFlow.asLiveData()

    private var registerResponse= MutableLiveData<RegisterUserMutation.Register>()
    val regResponse: LiveData<RegisterUserMutation.Register> get()= registerResponse

    private var loginResponse= MutableLiveData<LoginUserMutation.Login>()
    val logResponse: LiveData<LoginUserMutation.Login> get()= loginResponse

    init {
        Log.i(TAG, "ViewModel started...")
    }

    fun registerNewUser() {
        viewModelScope.launch {
            registerResponse.value = try {
                gardenBotRepository.registerNewUser(
                    RegisterInput(
                        "riquelme",
                        "1234",
                        "1234",
                        "riquelme@deboquita.com"
                    )
                )
            } catch (exception: ApolloException) {
                exception.printStackTrace()
                return@launch
            }
        }

    }

    fun loginUser() {
        viewModelScope.launch {
            loginResponse.value = try {
                gardenBotRepository.loginUser(
                    username = "manuelo",
                    password = "123456"
                )
            } catch (e: ApolloException) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    companion object {
        const val TAG = "VIEWMODEL"
    }

    sealed class GardenBotEvents

}