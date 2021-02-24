package com.example.gardenbotapp.ui.register

import android.content.Context
import androidx.lifecycle.*
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.data.GardenBotRepository
import com.example.gardenbotapp.type.RegisterInput
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val gardenBotRepository: GardenBotRepository,
    private val state: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private var registerResponse = MutableLiveData<RegisterUserMutation.Register>()
    val regResponse: LiveData<RegisterUserMutation.Register> get() = registerResponse
    private val regEventsChannel = Channel<RegisterEvents>()
    val regEvents = regEventsChannel.receiveAsFlow()

    var username = state.get<String>("username")
        set(value) {
            field = value
            state.set("username", value)
        }
    var password = state.get<String>("password")
        set(value) {
            field = value
            state.set("password", value)
        }
    var confirmPassword = state.get<String>("confirmPassword")
        set(value) {
            field = value
            state.set("confirmPassword", value)
        }
    var email = state.get<String>("email")
        set(value) {
            field = value
            state.set("email", value)
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

    sealed class RegisterEvents {
        object OnRegisterSuccess : RegisterEvents()
        data class RegisterError(val message: String) : RegisterEvents()
    }
}