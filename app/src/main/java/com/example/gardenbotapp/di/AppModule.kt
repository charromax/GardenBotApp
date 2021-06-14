/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.di

import android.content.Context
import com.example.gardenbotapp.data.domain.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideGardenBotRepository(): GardenBotRepository = GardenBotRepositoryImpl()

    @Provides
    fun provideChartRepository(): ChartRepository = ChartRepositoryImpl()

    @Provides
    fun provideLoginRepository(): LoginUserRepository = LoginUserRepositoryImpl()

    @Provides
    fun provideRegisterRepository(): RegisterUserRepository = RegisterUserRepositoryImpl()

    @Provides
    fun provideManualControlRepository(): ManualControlRepository = ManualControlRepositoryImpl()

    @Provides
    fun provideNotificationsRepository(): NotificationsRepository = NotificationsRepositoryImpl()

    @Provides
    fun provideOnboardingRepository(): OnboardingRepository = OnboardingRepositoryImpl()

    @Provides
    fun provideContext(@ActivityContext context: Context): Context = context
}
