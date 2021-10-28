package com.example.gardenbotapp.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.gardenbotapp.data.domain.MainRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.di.ApplicationIoScope
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationIoScope
    private val scope: CoroutineScope,
    private val preferencesManager: PreferencesManager,
    private val mainRepository: MainRepository
) : GardenBotBaseViewModel() {
    val isValidToken = MutableLiveData<Boolean>()
    fun checkAutologin() {
        scope.launch {
            try {
                val currentToken = preferencesManager.tokenFlow.first()
                if (currentToken.isEmpty()) isValidToken.postValue(false)
                isValidToken.postValue(mainRepository.checkToken(currentToken))
                if (isValidToken.value == false) {
                    preferencesManager.updateToken("")
                }
            } catch (e: Exception) {
                Log.i("MAIN_VIEWMODEL", "checkAutologin: ${e.message}")
                preferencesManager.updateToken("")
                isValidToken.postValue(false)
            }
        }
    }
}