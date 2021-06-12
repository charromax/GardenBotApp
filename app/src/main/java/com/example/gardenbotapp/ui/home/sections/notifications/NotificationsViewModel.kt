/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.notifications

import androidx.lifecycle.*
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.remote.NotificationsRepositoryImpl
import com.example.gardenbotapp.data.remote.model.Notification
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
    private val notificationsRepositoryImpl: NotificationsRepositoryImpl,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
) : ViewModel() {

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
                notificationsRepositoryImpl.newNotificationSub(deviceId)
                    .retryWhen { _, attempt ->
                        delay((attempt * 1000))    //exp delay
                        true
                    }
                    .collect { res ->
                        if (res.hasErrors() || res.data?.newNotification == null) {
                            notificationsChannel.send(Errors.SubError("ERROR: ${res.errors?.map { it.message }}"))
                        } else {
                            val notification = Notification(
                                createdAt = res.data?.newNotification?.createdAt,
                                code = res.data?.newNotification?.code,
                                message = res.data?.newNotification?.message
                            )
                            _notificationSub.value = notification
                        }
                    }
            } catch (e: ApolloException) {
                notificationsChannel.send(Errors.SubError("ERROR: ${e.message}"))
            }
        }
    }

}