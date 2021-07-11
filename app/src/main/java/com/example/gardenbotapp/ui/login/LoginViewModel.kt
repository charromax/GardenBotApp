/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.login

import android.content.Context
import androidx.lifecycle.*
import com.example.gardenbotapp.LoginUserMutation
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.domain.LoginUserRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserRepository: LoginUserRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : GardenBotBaseViewModel() {
    private val prefs = preferencesManager.preferencesFlow
    private val loginResponse = MutableLiveData<LoginUserMutation.Login>()
    val logResponse: LiveData<LoginUserMutation.Login> get() = loginResponse

    val token: LiveData<String> = preferencesManager.tokenFlow.asLiveData()


    var username = state.get<String>("username") ?: ""
        set(value) {
            field = value
            state.set("username", value)
        }
    var password = state.get<String>("password") ?: ""
        set(value) {
            field = value
            state.set("password", value)
        }

    private val loginEventsChannel = Channel<LoginEvents>()
    val logEvents = loginEventsChannel.receiveAsFlow()


    fun loginUser(context: Context) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                loginEventsChannel.send(LoginEvents.LoginError(context.getString(R.string.empty_fields_message)))
                return@launch
            } else {
                loginResponse.value = try {
                    loginUserRepository.loginUser(
                        username,
                        password
                    )
                } catch (e: Exception) {
                    loginEventsChannel.send(LoginEvents.LoginError(e.message!!))
                    return@launch
                }
            }

        }
    }

    fun updatePreferences(login: LoginUserMutation.Login) {
        viewModelScope.launch {
            preferencesManager.updateToken(login.token)
            preferencesManager.updateUserId(login.id)
            preferencesManager.updateDevice(login.devices[0]?.id ?: "")
        }
    }

    sealed class LoginEvents {
        data class LoginError(val message: String) : LoginEvents()

    }

}