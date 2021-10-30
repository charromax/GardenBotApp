/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.data.domain.NotificationsRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.model.Notification
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import com.example.gardenbotapp.util.Errors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : GardenBotBaseViewModel() {

    private val notificationsChannel = Channel<Errors>()
    val notificationsEvents = notificationsChannel.receiveAsFlow()

    private val _notificationSub = MutableLiveData<Notification>()
    val notificationSub: LiveData<Notification> get() = _notificationSub

    init {
        subscribeToMeasures()
    }

    private fun subscribeToMeasures() {
        viewModelScope.launch {
            try {
                val deviceId = preferencesManager.deviceIdFlow.first()
                notificationsRepository.newNotificationSub(deviceId)
                    .retryWhen { _, attempt ->
                        delay((attempt * 1000))    //exp delay
                        true
                    }
                    .collect { res ->
                        if (res.hasErrors() || res.data?.newNotification == null) {
                            notificationsChannel.send(Errors.SubError("ERROR: ${res.errors?.map { it.message }}"))
                        } else {
                            _notificationSub.value = Notification.fromMutation(res)
                        }
                    }
            } catch (e: ApolloException) {
                notificationsChannel.send(Errors.SubError("ERROR: ${e.message}"))
            }
        }
    }

}