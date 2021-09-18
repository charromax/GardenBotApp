/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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

const val USER_PREFS = "user_prefs"

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {
    private val Context.dataStore by preferencesDataStore(USER_PREFS)
    private val dataStore = context.dataStore

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

    suspend fun updateToken(token: String?) {
        token?.let {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.TOKEN] = token
            }
        }
    }

    suspend fun updateUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_ID] = userId
        }
    }

    val deviceIdFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            it[PreferencesKeys.SELECTED_DEVICE] ?: ""
        }


    val tokenFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            it[PreferencesKeys.TOKEN] ?: ""
        }

    val userIdFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            it[PreferencesKeys.USER_ID] ?: ""
        }

    private object PreferencesKeys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val SELECTED_CATEGORY = stringPreferencesKey("selected_category")
        val SELECTED_DEVICE = stringPreferencesKey("selected_device")
        val USER_ID = stringPreferencesKey("user_id")
        val TOKEN = stringPreferencesKey("token")
    }


}