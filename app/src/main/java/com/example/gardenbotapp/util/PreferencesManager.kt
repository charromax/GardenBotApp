/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.util

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

enum class SortOrder { BY_NAME, BY_DATE }

enum class Categories { AIR_TEMP, AIR_HUM, SOIL_HUM, ALL }

data class FilterPreferences(
    val order: SortOrder,
    val category: Categories,
    val deviceId: String,
    val userId: String,
    val token: String
)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore("user_prefs")

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val sortOrder = SortOrder.valueOf(
                prefs[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )
            val selectedCategory = Categories.valueOf(
                prefs[PreferencesKeys.SELECTED_CATEGORY] ?: Categories.ALL.name
            )
            val selectedDevice = prefs[PreferencesKeys.SELECTED_DEVICE] ?: ""
            val userId = prefs[PreferencesKeys.USER_ID] ?: ""
            val token = prefs[PreferencesKeys.TOKEN] ?: ""
            FilterPreferences(sortOrder, selectedCategory, selectedDevice, userId, token)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateCategory(category: Categories) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.SELECTED_CATEGORY] = category.name
        }
    }

    suspend fun updateDevice(deviceId: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.SELECTED_DEVICE] = deviceId
        }
    }

    suspend fun updateToken(token: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.TOKEN] = token
        }
    }

    suspend fun updateUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_ID] = userId
        }
    }

    val tokenFlow: Flow<String> = dataStore.data.map {
        it[PreferencesKeys.TOKEN] ?: ""
    }
    val userIdFlow: Flow<String> = dataStore.data.map {
        it[PreferencesKeys.USER_ID] ?: ""
    }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val SELECTED_CATEGORY = preferencesKey<String>("selected_category")
        val SELECTED_DEVICE = preferencesKey<String>("selected_device")
        val USER_ID = preferencesKey<String>("user_id")
        val TOKEN = preferencesKey<String>("token")
    }


}