/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.login

import android.content.Context
import androidx.lifecycle.*
import com.example.gardenbotapp.LoginUserMutation
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.GardenBotRepository
import com.example.gardenbotapp.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val gardenBotRepository: GardenBotRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val prefs = preferencesManager.preferencesFlow
    private var loginResponse = MutableLiveData<LoginUserMutation.Login>()
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


    fun loginUser() {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                loginEventsChannel.send(LoginEvents.LoginError(context.getString(R.string.empty_fields_message)))
                return@launch
            } else {
                loginResponse.value = try {
                    gardenBotRepository.loginUser(
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
        }
    }

    sealed class LoginEvents {
        data class LoginError(val message: String) : LoginEvents()

    }

}