/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.register

import com.example.gardenbotapp.FakeGardenbotRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class RegisterViewModelTest {
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        viewModel = RegisterViewModel(FakeGardenbotRepository(), FakeRegisterRepository())
    }

    @Test
    fun `when some empty value is set throw error`() {

    }
}