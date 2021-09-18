/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.register

import android.content.Context
import androidx.lifecycle.*
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.R
import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.data.domain.RegisterUserRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.type.RegisterInput
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserRepository: RegisterUserRepository,
    private val state: SavedStateHandle,
    private val preferencesManager: PreferencesManager,
    @ApplicationContext private val context: Context
) : GardenBotBaseViewModel() {
    private var registerResponse = MutableLiveData<RegisterUserMutation.Register>()
    val regResponse: LiveData<RegisterUserMutation.Register> get() = registerResponse
    private val regEventsChannel = Channel<RegisterEvents>()
    val regEvents = regEventsChannel.receiveAsFlow()

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
    var confirmPassword = state.get<String>("confirmPassword") ?: ""
        set(value) {
            field = value
            state.set("confirmPassword", value)
        }
    var email = state.get<String>("email") ?: ""
        set(value) {
            field = value
            state.set("email", value)
        }

    fun registerNewUser() {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank() || confirmPassword.isBlank() || email.isBlank()) {
                regEventsChannel.send(RegisterEvents.RegisterError(context.getString(R.string.all_fields_required_message)))
            } else {
                registerResponse.value = try {
                    registerUserRepository.registerNewUser(
                        RegisterInput(
                            username,
                            password,
                            confirmPassword,
                            email
                        )
                    )
                } catch (exception: ApolloException) {
                    exception.printStackTrace()
                    return@launch
                }
            }
        }
    }

    fun updatePreferences(id: String, token: String) {
        viewModelScope.launch {
            preferencesManager.updateUserId(id)
            preferencesManager.updateToken(token)
        }
    }

    sealed class RegisterEvents {
        data class RegisterError(val message: String) : RegisterEvents()
    }
}