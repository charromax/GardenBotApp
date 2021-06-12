/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.di

import android.content.Context
import com.example.gardenbotapp.data.remote.GardenBotRepository
import com.example.gardenbotapp.data.remote.NotificationsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGardenBotRepository() = GardenBotRepository()

    @Provides
    @Singleton
    fun provideNotificationsRepository() = NotificationsRepositoryImpl()

    @Provides
    fun provideContext(@ActivityContext context: Context): Context = context
}
