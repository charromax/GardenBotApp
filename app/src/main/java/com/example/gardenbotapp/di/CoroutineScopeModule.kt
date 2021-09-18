/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {

    @ApplicationDefaultScope
    @Provides
    fun provideApplicationDefaultScope(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(dispatcher)

    @ApplicationIoScope
    @Provides
    fun provideApplicationIoScope(
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(dispatcher)

    @ApplicationMainScope
    @Provides
    fun provideApplicationMainScope(
        @MainDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(dispatcher)
}