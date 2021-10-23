/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.di

import com.example.gardenbotapp.data.domain.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
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
    fun provideAutoPilotParamsRepository(): ParametersRepository
}