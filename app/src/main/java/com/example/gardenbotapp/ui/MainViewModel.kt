package com.example.gardenbotapp.ui

import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.di.ApplicationIoScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationIoScope
    private val scope: CoroutineScope,
    private val preferencesManager: PreferencesManager,
) {
}