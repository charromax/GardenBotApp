/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.register

import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.FakeGardenbotRepository
import com.example.gardenbotapp.RegisterUserMutation
import com.example.gardenbotapp.data.domain.RegisterUserRepository
import com.example.gardenbotapp.type.RegisterInput

class FakeRegisterRepository : RegisterUserRepository, FakeGardenbotRepository() {

    var shouldShowError = false

    override suspend fun registerNewUser(userInput: RegisterInput): RegisterUserMutation.Register? {
        if (shouldShowError.not()) {
            return RegisterUserMutation.Register(
                id = "60841d1a96093b00156b6362",
                createdAt = "2021-04-24T13:28:58.506Z",
                token = "someTOken",
                username = "charr0max"
            )
        } else {
            // throw fake error
            throw ApolloException("Fake Apollo Error")
        }
    }
}